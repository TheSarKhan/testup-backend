package com.exam.examapp.dto.response.information;

import com.exam.examapp.model.Pack;
import com.exam.examapp.model.information.Superiority;
import java.util.List;

public record AboutResponse(
        List<MediaContentResponse> banners,
        List<Superiority> superiorsForTeacher,
        List<MediaContentResponse> sliders,
        List<Superiority> superiorsForStudent,
        List<Pack> packs
) {
}
