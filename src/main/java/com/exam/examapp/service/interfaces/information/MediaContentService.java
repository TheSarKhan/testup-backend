package com.exam.examapp.service.interfaces.information;

import com.exam.examapp.dto.request.information.MediaContentRequest;
import com.exam.examapp.dto.request.information.MediaContentUpdateRequest;
import com.exam.examapp.dto.response.information.MediaContentResponse;
import com.exam.examapp.model.enums.PageType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaContentService {
    void saveMediaContent(MediaContentRequest request, MultipartFile image);

    List<MediaContentResponse> getAllMediaContent();

    List<MediaContentResponse> getMediaContentsByPageType(PageType pageType);

    MediaContentResponse getMediaContentById(UUID id);

    void updateMediaContent(MediaContentUpdateRequest request, MultipartFile image);

    void deleteMediaContent(UUID id);
}
