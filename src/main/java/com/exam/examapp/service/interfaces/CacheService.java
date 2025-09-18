package com.exam.examapp.service.interfaces;

public interface CacheService {
    void saveContent(String header, String headerPart2, String content, Long expiresIn);

    String getContent(String header, String headerPart2);

    void deleteContent(String header, String headerPart2) ;
}
