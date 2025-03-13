package com.chatapp.chatbackend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;
    private String id;
    private String timestamp;
}
