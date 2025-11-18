package com.exam.examapp.dto.response.subject;

import com.exam.examapp.model.exam.Module;
import com.exam.examapp.model.exam.Submodule;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public record SubmoduleProjection(UUID id, String name, UUID moduleId, String moduleName, String moduleLogoUrl,
                                  Instant moduleCreatedAt, Instant moduleUpdatedAt, String logoUrl, Instant createdAt,
                                  Instant updatedAt, String getSubjectNames, Long getExamCount) {
    public SubmoduleResponse toSubmoduleResponse() {
        return new SubmoduleResponse(
                new Submodule(
                        this.id,
                        this.name,
                        new Module(
                                this.moduleId,
                                this.moduleName,
                                this.moduleLogoUrl,
                                this.moduleCreatedAt,
                                this.moduleUpdatedAt),
                        this.logoUrl,
                        this.createdAt,
                        this.updatedAt),
                Arrays.asList(this.getSubjectNames.split("!")),
                this.getExamCount);
    }
}
