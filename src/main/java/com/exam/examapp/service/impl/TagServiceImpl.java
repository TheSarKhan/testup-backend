package com.exam.examapp.service.impl;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Tag;
import com.exam.examapp.repository.TagRepository;
import com.exam.examapp.service.interfaces.TagService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
  private final TagRepository tagRepository;

  @Override
  public void createTag(String tagName) {
    tagRepository.save(Tag.builder().tagName(tagName).build());
  }

  @Override
  public List<Tag> getAllTags() {
      List<Tag> all = tagRepository.findAll();

      List<Tag> list = all.stream().filter(tag -> tag.getTagName() != null).toList();

      return all;
  }

  @Override
  public Tag getTagByName(String tagName) {
    return tagRepository
        .getByTagName(tagName)
        .orElseThrow(() -> new ResourceNotFoundException("Tag not found."));
  }

  @Override
  public Tag getTagById(UUID id) {
    return tagRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tag not found."));
  }

  @Override
  public void updateTag(UUID id, String tagName) {
    Tag tag = getTagById(id);
    if (!getTagByName(tagName).getId().equals(id))
      throw new BadRequestException("Tag already exists.");
    tag.setTagName(tagName);
    tagRepository.save(tag);
  }

  @Override
  public void deleteTag(UUID id) {
    tagRepository.deleteById(id);
  }
}
