package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.dto.request.subject.TopicUpdateRequest;
import com.exam.examapp.model.subject.Topic;

import java.util.List;
import java.util.UUID;

public interface TopicService {
    void save(UUID subjectId, String name);

    List<Topic> getAll();

    List<Topic> getAllBySubjectId(UUID subjectId);

    Topic getById(UUID id);

    Topic getByName(String name);

    void update(TopicUpdateRequest request);

    void delete(UUID id);
}
