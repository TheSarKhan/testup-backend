package com.exam.examapp.service.impl;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Tag;
import com.exam.examapp.repository.TagRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.TagService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    private final LogService logService;

    private final UserService userService;

    @Override
    public void createTag(String tagName) {
        log.info("Etiket yaradılır: {}", tagName);
        tagRepository.save(Tag.builder().tagName(tagName).build());
        log.info("Etiket yaradıldı");
        logService.save("Etiketlər yaradıldı:" + tagName, userService.getCurrentUserOrNull());
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getTagByName(String tagName) {
        return tagRepository
                .getByTagName(tagName)
                .orElseThrow(() -> new ResourceNotFoundException("Etiket tapılmadı"));
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiket tapılmadı"));
    }

    @Override
    public void updateTag(UUID id, String tagName) {
        log.info("Etiket yeniləir:{}", tagName);
        Tag tag = getTagById(id);
        if (tagRepository.existsByTagName(tagName))
            throw new BadRequestException("Etiket movcuddur");
        tag.setTagName(tagName);
        tagRepository.save(tag);
        log.info("Etiket yeniləndi:{}", tagName);
        logService.save("Etiket yeniləndi:" + tagName, userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteTag(UUID id) {
        log.info("Etiket silinir");
        tagRepository.deleteById(id);
        log.info("Etiket silindi");
        logService.save("Etiket silindi", userService.getCurrentUserOrNull());
    }
}
