package com.exam.examapp.mapper.information;

import com.exam.examapp.dto.request.information.SuperiorityRequest;
import com.exam.examapp.dto.request.information.SuperiorityUpdateRequest;
import com.exam.examapp.model.information.Superiority;

public class SuperiorityMapper {
    public static Superiority requestTo(SuperiorityRequest request){
        return Superiority.builder()
                .text(request.text())
                .type(request.type())
                .build();
    }

    public static Superiority updateRequestTo(Superiority superiority, SuperiorityUpdateRequest request){
        superiority.setText(request.text());
        superiority.setType(request.type());
        return superiority;
    }
}
