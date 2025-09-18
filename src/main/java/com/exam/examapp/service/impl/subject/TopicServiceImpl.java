package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.TopicUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.repository.subject.TopicRepository;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    private final SubjectService subjectService;

    @Override
    public void save(UUID subjectId, String name) {
        if (topicRepository.existsByName(name))
            throw new BadRequestException("Topic already exists");

        Subject subject = subjectService.getById(subjectId);

        Topic topic = Topic.builder().name(name).subject(subject).build();

        topicRepository.save(topic);
    }

    @Override
    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    @Override
    public List<Topic> getAllBySubjectId(UUID subjectId) {
        return topicRepository.findAllBySubject_Id(subjectId);
    }

    @Override
    public Topic getById(UUID id) {
        return topicRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Topic not found"));
    }

    @Override
    public Topic getByName(String name) {
        return topicRepository.getTopicByName(name).orElseThrow(()->
                new ResourceNotFoundException("Topic not found"));
    }

    @Override
    public void update(TopicUpdateRequest request) {
        Topic topic = getById(request.id());
        Optional<Topic> topicByName = topicRepository.getTopicByName(request.name());
        if (topicByName.isPresent() && !topicByName.get().getId().equals(request.id()))
            throw new BadRequestException("Topic already exists");
        topic.setName(request.name());

        Subject subject = subjectService.getById(request.subjectId());
        topic.setSubject(subject);

        topicRepository.save(topic);
    }

    @Override
    public void delete(UUID id) {
        topicRepository.deleteById(id);
    }
}
