package com.exam.examapp.repository.subject;

import com.exam.examapp.model.exam.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    boolean existsModuleByName(String name);

    Optional<Module> getModuleByName(String name);
}
