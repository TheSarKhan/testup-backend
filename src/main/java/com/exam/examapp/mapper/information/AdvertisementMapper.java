package com.exam.examapp.mapper.information;

import com.exam.examapp.dto.request.information.AdvertisementRequest;
import com.exam.examapp.dto.request.information.AdvertisementUpdateRequest;
import com.exam.examapp.model.information.Advertisement;

public class AdvertisementMapper {
    public static Advertisement requestTo(AdvertisementRequest request) {
        return Advertisement.builder()
                .title(request.title())
                .description(request.description())
                .backgroundColor(request.backgroundColor())
                .buttonColor(request.buttonColor())
                .textColor(request.textColor())
                .redirectUrl(request.redirectUrl())
                .build();
    }

    public static Advertisement updateRequestTo(Advertisement advertisement,
                                                AdvertisementUpdateRequest request) {
        advertisement.setTitle(request.title());
        advertisement.setDescription(request.description());
        advertisement.setBackgroundColor(request.backgroundColor());
        advertisement.setButtonColor(request.buttonColor());
        advertisement.setTextColor(request.textColor());
        advertisement.setRedirectUrl(request.redirectUrl());
        return advertisement;
    }
}