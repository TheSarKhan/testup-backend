package com.exam.examapp.repository;

import com.exam.examapp.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    @Query("from Log where deletedAt is null order by createdAt limit :size offset :skip")
    List<Log> getAllOrderByCreatedAt(int skip, int size);
}
