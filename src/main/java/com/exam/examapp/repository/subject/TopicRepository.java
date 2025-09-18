package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    boolean existsByName(String name);

    List<Topic> findAllBySubject_Id(UUID subjectId);

    Optional<Topic> getTopicByName(String name);
}
