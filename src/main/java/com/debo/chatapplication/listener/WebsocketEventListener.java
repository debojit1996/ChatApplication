package com.debo.chatapplication.listener;

import java.util.Objects;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.debo.chatapplication.constants.ChatAppConstants;
import com.debo.chatapplication.model.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final SimpMessageSendingOperations messageOperations;

    @EventListener
    public void handleWebsocketDisconnectHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (Objects.nonNull(username)) {
            logger.info("User disconnected: {}", username);

            messageOperations.convertAndSend("/topic/chat", Message.builder()
                    .type(ChatAppConstants.MsgType.LEAVE)
                    .sender(username)
                    .build());
        }
    }
}
