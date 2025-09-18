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
import com.exam.examapp.service.interfaces.information.MediaContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaContentServiceImpl implements MediaContentService {
    private final static String IMAGE_PATH = "uploads/images/media_contents";

    private final MediaContentRepository mediaContentRepository;

    private final LocalFileServiceImpl fileService;

    @Override
    public void saveMediaContent(MediaContentRequest request, MultipartFile image) {
        if (mediaContentRepository.existsMediaContentByText(request.text()))
            throw new BadRequestException("Media Content with text " + request.text() + " already exists.");

        MediaContent mediaContent = MediaContentMapper.requestTo(request);
        mediaContent.setPictureUrl(fileService.uploadFile(IMAGE_PATH, image));
        mediaContentRepository.save(mediaContent);
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
        Optional<MediaContent> mediaContentByText = mediaContentRepository.getMediaContentByText(request.text());
        MediaContent mediaContent = getById(request.id());

        if (mediaContentByText.isPresent() && !request.id().equals(mediaContentByText.get().getId()))
            throw new BadRequestException("Media Content with text " + request.text() + " already exists.");

        MediaContent updatedMediaContent = MediaContentMapper.updateRequestTo(mediaContent, request);
        fileService.deleteFile(IMAGE_PATH, mediaContent.getPictureUrl());
        updatedMediaContent.setPictureUrl(fileService.uploadFile(IMAGE_PATH, image));
        mediaContentRepository.save(updatedMediaContent);
    }

    @Override
    public void deleteMediaContent(UUID id) {
        MediaContent mediaContent = getById(id);
        fileService.deleteFile(IMAGE_PATH, mediaContent.getPictureUrl());
        mediaContentRepository.deleteById(id);
    }

    private MediaContent getById(UUID id) {
        return mediaContentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Media Content not found."));
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
