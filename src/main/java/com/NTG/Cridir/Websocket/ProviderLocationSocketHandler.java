package com.NTG.Cridir.Websocket;

import com.NTG.Cridir.model.Location;
import com.NTG.Cridir.model.ServiceRequest;
import com.NTG.Cridir.repository.ServiceRequestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProviderLocationSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceRequestRepository requestRepository;

    // providerId -> customer session (واحد بس لكل provider في الوقت الحالي)
    private final Map<Long, WebSocketSession> providerToCustomer = new ConcurrentHashMap<>();

    public ProviderLocationSocketHandler(ServiceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode node = objectMapper.readTree(message.getPayload());
        String type = node.get("type").asText();

        switch (type) {
            case "SUBSCRIBE" -> handleSubscribe(session, node);
            case "LOCATION_UPDATE" -> handleLocationUpdate(node);
            default -> System.out.println("⚠️ Unknown message type: " + type);
        }
    }

    private void handleSubscribe(WebSocketSession session, JsonNode node) throws Exception {
        Long requestId = node.get("requestId").asLong();

        // 1) هات الطلب من الداتابيز
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Long providerId = request.getProvider().getProviderId();

        // 2) اربط الـ Customer ده بالـ Provider
        providerToCustomer.put(providerId, session);

        System.out.println("✅ Customer subscribed to provider " + providerId);
    }

    public void broadcastProviderLocation(Long providerId, Location loc) {
        try {
            WebSocketSession customerSession = providerToCustomer.get(providerId);
            if (customerSession != null && customerSession.isOpen()) {
                String payload = objectMapper.writeValueAsString(Map.of(
                        "providerId", providerId,
                        "lat", loc.getLatitude(),
                        "lng", loc.getLongitude()
                ));
                customerSession.sendMessage(new TextMessage(payload));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLocationUpdate(JsonNode node) throws Exception {
        Long providerId = node.get("providerId").asLong();
        Double lat = node.get("lat").asDouble();
        Double lng = node.get("lng").asDouble();

        System.out.println("📍 Provider " + providerId + " location → " + lat + "," + lng);

        // 3) ابعت للـ Customer المرتبط بالـ Provider ده
        Location loc = new Location();
        loc.setLatitude(lat);
        loc.setLongitude(lng);

        broadcastProviderLocation(providerId, loc);
    }


    // ✨ method تقدر تستخدمها من أي حتة (service/controller) عشان تبعت لوكيشن


    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        // لو الـ Customer ده اتقفل، شيله من الماب
        providerToCustomer.values().removeIf(s -> s.equals(session));
    }
}
