package com.exam.examapp.repository.subject;

import com.exam.examapp.model.exam.Module;
import com.exam.examapp.model.exam.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmoduleRepository extends JpaRepository<Submodule, UUID> {
    List<Submodule> getAllByModule_Id(UUID moduleId);

    Optional<Submodule> getByName(String name);

    boolean existsByNameAndModule(String name, Module module);
}
