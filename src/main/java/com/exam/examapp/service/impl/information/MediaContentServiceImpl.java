package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.MediaContentRequest;
import com.exam.examapp.dto.request.information.MediaContentUpdateRequest;
import com.exam.examapp.dto.response.information.MediaContentResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.MediaContentMapper;
import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.model.information.MediaContent;
import com.exam.examapp.repository.information.MediaContentRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.information.MediaContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaContentServiceImpl implements MediaContentService {
    private final static String IMAGE_PATH = "uploads/images/media_contents";

    private final MediaContentRepository mediaContentRepository;

    private final UserService userService;

    private final LocalFileServiceImpl fileService;

    private final LogService logService;

    @Override
    public void saveMediaContent(MediaContentRequest request, MultipartFile image) {
        log.info("Media məzmunu yaradılır");
        if (mediaContentRepository.existsMediaContentByText(request.text()))
            throw new BadRequestException(request.text() + " mətnli media məzmunu artıq mövcuddur");

        MediaContent mediaContent = MediaContentMapper.requestTo(request);
        mediaContent.setPictureUrl(fileService.uploadFile(IMAGE_PATH, image));
        mediaContentRepository.save(mediaContent);
        log.info("Media məzmunu yaradıldı");
        logService.save("Media məzmunu yaradıldı", userService.getCurrentUserOrNull());
    }

    @Override
    public List<MediaContentResponse> getAllMediaContent() {
        return mediaContentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MediaContentResponse> getMediaContentsByPageType(PageType pageType) {
        return mediaContentRepository.getMediaContentByPageType(pageType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MediaContentResponse getMediaContentById(UUID id) {
        return toResponse(getById(id));
    }

    @Override
    public void updateMediaContent(MediaContentUpdateRequest request, MultipartFile image) {
        log.info("Media məzmunu yenilənir");
        Optional<MediaContent> mediaContentByText = mediaContentRepository.getMediaContentByText(request.text());
        MediaContent mediaContent = getById(request.id());

        if (mediaContentByText.isPresent() && !request.id().equals(mediaContentByText.get().getId()))
            throw new BadRequestException(request.text() + " mətnli media məzmunu artıq mövcuddur.");

        MediaContent updatedMediaContent = MediaContentMapper.updateRequestTo(mediaContent, request);
        if (image != null) {
            fileService.deleteFile(IMAGE_PATH, mediaContent.getPictureUrl());
            updatedMediaContent.setPictureUrl(fileService.uploadFile(IMAGE_PATH, image));
        }
        mediaContentRepository.save(updatedMediaContent);
        log.info("Media məzmunu yeniləndi");
        logService.save("Media məzmunu yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteMediaContent(UUID id) {
        log.info("Media məzmunu silinir");
        MediaContent mediaContent = getById(id);
        fileService.deleteFile(IMAGE_PATH, mediaContent.getPictureUrl());
        mediaContentRepository.deleteById(id);
        log.info("Media məzmunu silindi");
        logService.save("Media məzmunu silindi", userService.getCurrentUserOrNull());
    }

    private MediaContent getById(UUID id) {
        return mediaContentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Media məzmunu tapılmadı"));
    }

    private MediaContentResponse toResponse(MediaContent mediaContent) {
        return new MediaContentResponse(
                mediaContent.getId(),
                mediaContent.getText(),
                mediaContent.getPictureUrl(),
                mediaContent.getAuthor(),
                mediaContent.getBackgroundColor(),
                mediaContent.getTextColor(),
                mediaContent.getPageType(),
                mediaContent.getCreatedAt(),
                mediaContent.getUpdatedAt());
    }
}
