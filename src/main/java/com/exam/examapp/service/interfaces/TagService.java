package com.exam.examapp.service.interfaces;

import com.exam.examapp.model.Tag;

import java.util.List;
import java.util.UUID;

public interface TagService {
    void createTag(String tagName);

    List<Tag> getAllTags();

    Tag getTagByName(String tagName);

    Tag getTagById(UUID id);

    void updateTag(UUID id, String tagName);

    void deleteTag(UUID id);
}
