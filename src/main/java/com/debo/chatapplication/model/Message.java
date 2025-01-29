package com.debo.chatapplication.model;

import com.debo.chatapplication.constants.ChatAppConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    private ChatAppConstants.MsgType type;

    private String content;

    private String sender;
}
