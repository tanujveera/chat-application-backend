package com.chatapp.chatbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // adding websocket endpoint and sockJS for error fallback
        // For secure websockets use "wss"
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/ws-native").setAllowedOrigins("*");
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Add the application prefix path
        registry.setApplicationDestinationPrefixes("/app");
        // Message broker endpoint where users will subscribe to get all messages from this endpoint
        registry.enableSimpleBroker("/topic");

    }
}
