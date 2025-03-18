package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Controller
public class WebSocketsController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CHAT_HISTORY_KEY = "chat:messages";

    // URL used to invoke this method is defined in this annotation
    // and it send that message to the topic of message broker
    @MessageMapping("/chat.sendMessage")
    //Removed SendTo as we are subscribing to websockets
//    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws JsonProcessingException {
        System.out.println("chatMessage "+chatMessage.toString());

        // Add extra details (ID & Timestamp)
        chatMessage.setId(UUID.randomUUID().toString());
        // Format timestamp to ISO 8601 string (UTC time)
//        String timestamp = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        String timestamp = new Date().toString();
        chatMessage.setTimestamp(timestamp);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            stringRedisTemplate.convertAndSend("chat:channel", messageJson);

            // Store the message in Redis
            redisTemplate.opsForList().rightPush(CHAT_HISTORY_KEY, messageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return chatMessage;
   }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
   public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        // add username in websocket session
        headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());
        return chatMessage;
   }
}

