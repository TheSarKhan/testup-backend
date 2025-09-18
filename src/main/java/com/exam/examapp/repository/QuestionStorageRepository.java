package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.question.QuestionStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionStorageRepository extends JpaRepository<QuestionStorage, UUID> {
  Optional<QuestionStorage> getByTeacher(User teacher);
}
