package com.exam.examapp.repository;

import com.exam.examapp.model.exam.ExamTemplate;

import java.util.List;
import java.util.UUID;

import com.exam.examapp.model.exam.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTemplateRepository extends JpaRepository<ExamTemplate, UUID> {
    List<ExamTemplate> getBySubmodule(Submodule submodule);
}
