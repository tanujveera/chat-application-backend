package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatHistoryController {

    private RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_HISTORY_KEY = "chat:messages";

    @Autowired
    public ChatHistoryController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/chat/history")
    public List<ChatMessage> getChatHistory() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Retrieve stored messages (as objects) from Redis
        List<Object> messageObjects = redisTemplate.opsForList().range(CHAT_HISTORY_KEY, 0, -1);

        if (messageObjects == null || messageObjects.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Object obj : messageObjects) {
            // Convert each object to a String
            String json = obj.toString();
            try {
                // Deserialize the JSON string into a ChatMessage object
                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
                chatMessages.add(message);
            } catch (JsonProcessingException e) {
                System.err.println("Error deserializing message: " + json);
                e.printStackTrace();
            }
        }
        return chatMessages;
    }

}
