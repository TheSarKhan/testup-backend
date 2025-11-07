package com.exam.examapp.repository;

import com.exam.examapp.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PackRepository extends JpaRepository<Pack, UUID> {
    Optional<Pack> getPackByPackName(String name);

    boolean existsPackByPackName(String packName);

    @Query("select packName from Pack")
    List<String> getAllPackNames();
}
