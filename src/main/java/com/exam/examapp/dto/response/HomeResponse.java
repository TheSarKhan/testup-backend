package com.exam.examapp.dto.response;

import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.information.MediaContentResponse;
import com.exam.examapp.model.Pack;
import com.exam.examapp.model.information.Advertisement;
import com.exam.examapp.model.information.Superiority;

import java.util.List;

public record HomeResponse(List<ExamBlockResponse> lastCreatedExams,
                           List<Advertisement> advertisements,
                           List<MediaContentResponse> sliders,
                           List<Superiority> superiors,
                           List<MediaContentResponse> banners,
                           List<Pack> packs) {
}
