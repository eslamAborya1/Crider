package com.NTG.Cridir.Config;

import com.NTG.Cridir.Websocket.RideSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RideSocketHandler rideSocketHandler;

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    public WebSocketConfig(RideSocketHandler rideSocketHandler) {
        this.rideSocketHandler = rideSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rideSocketHandler, "/ws/ride")
                .setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).toArray(String[]::new));
    }
}

