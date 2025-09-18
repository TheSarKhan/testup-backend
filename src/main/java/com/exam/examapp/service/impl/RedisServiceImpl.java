package com.exam.examapp.service.impl;

import com.exam.examapp.service.interfaces.CacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements CacheService {
    private final RedisTemplate redisTemplate;

    public RedisServiceImpl(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveContent(String header, String username, String refreshToken, Long expireIn) {
        String key = header + username;
        redisTemplate.opsForValue().set(key, refreshToken, expireIn, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getContent(String header, String username) {
        String key = header + username;
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteContent(String header, String username) {
        redisTemplate.delete(header + username);
    }
}
