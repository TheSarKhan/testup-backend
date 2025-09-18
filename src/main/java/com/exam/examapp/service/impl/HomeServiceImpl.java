package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.HomeResponse;
import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.service.interfaces.ExamService;
import com.exam.examapp.service.interfaces.HomeService;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.information.AdvertisementService;
import com.exam.examapp.service.interfaces.information.MediaContentService;
import com.exam.examapp.service.interfaces.information.SuperiorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final ExamService examService;

    private final AdvertisementService advertisementService;

    private final MediaContentService mediaContentService;

    private final SuperiorityService superiorityService;

    private final PackService packService;

    @Override
    public HomeResponse getHomeInfo() {
        return new HomeResponse(examService.getLastCreatedExams(),
                advertisementService.getAdvertisements(),
                mediaContentService.getMediaContentsByPageType(PageType.HOME_SLIDER),
                superiorityService.getSuperiorityBySuperiorityType(SuperiorityType.WHY_TEST_UP),
                mediaContentService.getMediaContentsByPageType(PageType.HOME_BANNER),
                packService.getAllPacks());
    }
}
