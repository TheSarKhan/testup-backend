package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.TopicUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.repository.subject.TopicRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    private final SubjectService subjectService;

    private final LogService logService;

    private final UserService userService;

    @Override
    public void save(UUID subjectId, String name) {
        log.info("Mövzu yaradılır");
        if (topicRepository.existsByName(name))
            throw new BadRequestException("Mövzu artıq mövcuddur");

        Subject subject = subjectService.getById(subjectId);

        Topic topic = Topic.builder().name(name).subject(subject).build();

        topicRepository.save(topic);
        log.info("Mövzu yaradıldı");
        logService.save("Mövzu yaradıldı", userService.getCurrentUserOrNull());
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
                new ResourceNotFoundException("Mövzu tapılmadı"));
    }

    @Override
    public Topic getByName(String name) {
        return topicRepository.getTopicByName(name).orElseThrow(()->
                new ResourceNotFoundException("Mövzu tapılmadı"));
    }

    @Override
    public void update(TopicUpdateRequest request) {
        log.info("Mövzu yenilənir");
        Topic topic = getById(request.id());
        Optional<Topic> topicByName = topicRepository.getTopicByName(request.name());
        if (topicByName.isPresent() && !topicByName.get().getId().equals(request.id()))
            throw new BadRequestException("Mövzu artıq mövcuddur");
        topic.setName(request.name());

        Subject subject = subjectService.getById(request.subjectId());
        topic.setSubject(subject);

        topicRepository.save(topic);
        log.info("Mövzu yeniləndi");
        logService.save("Mövzu yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void delete(UUID id) {
        log.info("Mövzu silinir");
        topicRepository.deleteById(id);
        log.info("Mövzu silindi");
        logService.save("Mövzu silindi", userService.getCurrentUserOrNull());
    }
}
