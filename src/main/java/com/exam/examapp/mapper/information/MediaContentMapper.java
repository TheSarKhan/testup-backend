package com.exam.examapp.mapper.information;

import com.exam.examapp.dto.request.information.MediaContentRequest;
import com.exam.examapp.dto.request.information.MediaContentUpdateRequest;
import com.exam.examapp.model.information.MediaContent;

public class MediaContentMapper {
    public static MediaContent requestTo(MediaContentRequest request) {
        return MediaContent.builder()
                .text(request.text())
                .author(request.author())
                .backgroundColor(request.backgroundColor())
                .textColor(request.textColor())
                .pageType(request.pageType())
                .build();
    }

    public static MediaContent updateRequestTo(MediaContent mediaContent, MediaContentUpdateRequest request) {
        mediaContent.setText(request.text());
        mediaContent.setAuthor(request.author());
        mediaContent.setBackgroundColor(request.backgroundColor());
        mediaContent.setTextColor(request.textColor());
        mediaContent.setPageType(request.pageType());
        return mediaContent;
    }
}
