package com.NTG.Cridir.Websocket;

import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.service.JwtService;
import com.NTG.Cridir.service.ServiceRequestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
@Slf4j
@Component
public class RideSocketHandler extends TextWebSocketHandler {
    private final ServiceRequestService serviceRequestService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // requestId → list of connected sessions (Customer + Provider)
    private final Map<Long, List<WebSocketSession>> activeRequests = new ConcurrentHashMap<>();
    //private static final Logger log = LoggerFactory.getLogger(RideSocketHandler.class);

    public RideSocketHandler(ServiceRequestService serviceRequestService, JwtService jwtService) {
        this.serviceRequestService = serviceRequestService;
        this.jwtService = jwtService;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery(); // e.g. ?token=xxx
        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6);

            try {
                String role = jwtService.extractRole(token);
                Long userId = jwtService.extractUserId(token);

                session.getAttributes().put("role", role);
                session.getAttributes().put("userId", userId);

                log.info(" WebSocket Auth Success → userId={} role={}", userId, role);

            } catch (Exception e) {
                log.warn(" Invalid token in WebSocket connection: {}", e.getMessage());
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
            }
        } else {
            log.warn(" Missing token in WebSocket connection");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing token"));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode node = objectMapper.readTree(message.getPayload());
        String type = node.get("type").asText();

        switch (type) {
            case "JOIN" -> handleJoin(session, node);
            case "LOCATION_UPDATE" -> handleLocationUpdate(session, node);
            case "CANCEL" -> handleCancel(session, node);
            case "STATUS_UPDATE" -> handleStatusUpdate(session, node);
            default -> System.out.println("Unknown type " + type);
        }
    }

    // لو الـ client قفل → نشيل الـ session من أي requestId
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeRequests.values().forEach(list -> list.remove(session));
    }

    private void handleJoin(WebSocketSession session, JsonNode node) {
        Long requestId = node.get("requestId").asLong();
        activeRequests.computeIfAbsent(requestId, k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("Session joined request " + requestId);
    }

    private void handleLocationUpdate(WebSocketSession session, JsonNode node) throws Exception {
        //  Check role (Provider فقط)
        String role = (String) session.getAttributes().get("role");
        if (!"PROVIDER".equalsIgnoreCase(role)) {
            System.out.println("Unauthorized LOCATION_UPDATE from non-provider");
            return;
        }

        Long requestId = node.get("requestId").asLong();
        double lat = node.get("lat").asDouble();
        double lng = node.get("lng").asDouble();

        broadcastLocation(requestId, lat, lng);
    }


    private void handleCancel(WebSocketSession session, JsonNode node) {
        String role = (String) session.getAttributes().get("role");
        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            log.warn("Unauthorized CANCEL from non-customer");
            return;
        }

        Long requestId = node.get("requestId").asLong();
        Long customerId = (Long) session.getAttributes().get("userId");

        serviceRequestService.cancelRequest(requestId, customerId);
    }

    private void handleStatusUpdate(WebSocketSession session, JsonNode node) throws Exception {
        Long requestId = node.get("requestId").asLong();
        String statusStr = node.get("status").asText();

        try {
            Status status = Status.valueOf(statusStr.toUpperCase());
            broadcast(requestId, Map.of(
                    "type", "STATUS_UPDATE",
                    "status", status.name()
            ));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status: " + statusStr);
        }
    }

    // ================== Public Broadcast Methods ==================

    public void broadcastLocation(Long requestId, double lat, double lng) throws Exception {
        broadcast(requestId, Map.of(
                "type", "LOCATION",
                "lat", lat,
                "lng", lng
        ));
    }

    public void broadcastCancel(Long requestId, String message) throws Exception {
        broadcast(requestId, Map.of(
                "type", "CANCEL",
                "message", message
        ));
    }

    public void broadcastGeneric(Long requestId, Map<String, Object> payload) throws Exception {
        broadcast(requestId, payload);
    }

    // ================== Private Core ==================

    private void broadcast(Long requestId, Object payload) throws Exception {
        List<WebSocketSession> sessions = activeRequests.get(requestId);
        if (sessions != null) {
            String json = objectMapper.writeValueAsString(payload);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(json));
                }
            }
        }
    }
}
