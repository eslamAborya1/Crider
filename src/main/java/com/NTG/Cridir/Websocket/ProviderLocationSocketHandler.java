package com.NTG.Cridir.Websocket;

import com.NTG.Cridir.model.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProviderLocationSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    // keep track of all sessions (simple demo broadcast)
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println(" WebSocket connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // if you expect providers to push their own location over WS, parse here
        ProviderLocationMessage location = objectMapper.readValue(message.getPayload(), ProviderLocationMessage.class);
        System.out.println(" Provider " + location.getProviderId() + " → " + location.getLat() + "," + location.getLng());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println(" WebSocket closed: " + session.getId());
    }

    public void broadcastProviderLocation(Long providerId, Location loc) {
        try {
            String payload = objectMapper.writeValueAsString(new Outgoing(providerId, loc.getLatitude(), loc.getLongitude()));
            TextMessage msg = new TextMessage(payload);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) s.sendMessage(msg);
            }
        } catch (Exception ignored) { }
    }

    //  DTO for outgoing messages
    private record Outgoing(Long providerId, Double lat, Double lng) {}
}
