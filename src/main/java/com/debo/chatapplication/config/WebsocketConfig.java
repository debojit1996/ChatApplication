package com.debo.chatapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /* This line defines the WebSocket endpoint.  /ws is the URL that clients will use to connect to the WebSocket server.
          withSockJS() enables SockJS fallback.
          SockJS is a library that provides a fallback mechanism for WebSockets. If the client's browser doesn't support WebSockets,
          SockJS will try other techniques (e.g., long polling, server-sent events) to establish a real-time connection. This ensures
          compatibility with a wider range of browsers. If you are certain that all your clients support WebSockets you can remove
          .withSockJS()
        */
        registry.addEndpoint("/ws").withSockJS();

    }

    // This method configures the message broker.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
          This sets the prefix for messages that are destined for application-handled methods (controllers with @MessageMapping).
          When a client sends a message to a destination that starts with /app, it will be routed to a controller method.
          For example, a client might send a message to /app/chat to be handled by a controller.
         */
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }
}
