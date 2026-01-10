package com.exam.examapp.service.impl;

import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.service.interfaces.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements CacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveContent(String header, String keyPart2, String data, Long expireIn) {
        String key = header + keyPart2;
        redisTemplate.opsForValue().set(key, data, expireIn, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getContent(String header, String keyPart2) {
        String key = header + keyPart2;
        String content = redisTemplate.opsForValue().get(key);
        if (content == null)
            throw new ResourceNotFoundException("Redisde bu key uzre data tapilmadi.");
        return content;
    }

    @Override
    public void deleteContent(String header, String keyPart2) {
        redisTemplate.delete(header + keyPart2);
    }
}
