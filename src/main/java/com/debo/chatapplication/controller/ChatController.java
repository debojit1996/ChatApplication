package com.debo.chatapplication.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.debo.chatapplication.model.Message;

@Controller
public class ChatController {

    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/chat")
    public Message sendMsg(@Payload Message msg) {
        return msg;
    }

    @MessageMapping("chat.addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message msg, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", msg.getSender());
        return msg;
    }
}
