package com.exam.examapp.repository;

import com.exam.examapp.model.Tag;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
  Optional<Tag> getByTagName(String tagName);

    boolean existsByTagName(String tagName);
}
