package com.sigma_squad.computify.shared.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig - Configuration for WebSocket and STOMP messaging
 * Enables real-time bidirectional communication with clients
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Register STOMP endpoint for WebSocket connections
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/notifications")
            .setAllowedOrigins("http://localhost:5173", "http://localhost:3000") // Frontend URLs
            .withSockJS();  // Fallback for browsers without WebSocket support
    }

    /**
     * Configure message broker for routing messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple in-memory message broker for /topic and /user destinations
        config.enableSimpleBroker("/topic", "/user");

        // Set application destination prefixes for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");

        // User destination prefix for sending messages to individual users
        config.setUserDestinationPrefix("/user");
    }
}
