package com.chatapp.chatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ChatHistoryController {

    private RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_HISTORY_KEY = "chat:messages";

    @Autowired
    public ChatHistoryController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/chat/history")
    public List<Object> getChatHistory() {
        // Retrieve messages from Redis
        System.out.println(redisTemplate.opsForList().range(CHAT_HISTORY_KEY, 0, -1));
        return redisTemplate.opsForList().range(CHAT_HISTORY_KEY, 0, -1);
    }
}
