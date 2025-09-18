package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.response.information.AboutResponse;
import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.information.AboutService;
import com.exam.examapp.service.interfaces.information.MediaContentService;
import com.exam.examapp.service.interfaces.information.SuperiorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {
    private final MediaContentService mediaContentService;

    private final SuperiorityService superiorityService;

    private final PackService packService;

    @Override
    public AboutResponse getAbout() {
        return new AboutResponse(
                mediaContentService.getMediaContentsByPageType(PageType.ABOUT_BANNER),
                superiorityService.getSuperiorityBySuperiorityType(SuperiorityType.ADVANTAGES_FOR_TEACHER),
                mediaContentService.getMediaContentsByPageType(PageType.ABOUT_SLIDER),
                superiorityService.getSuperiorityBySuperiorityType(SuperiorityType.ADVANTAGES_FOR_STUDENT),
                packService.getAllPacks());
    }
}
