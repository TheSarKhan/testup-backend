package com.exam.examapp.mapper.information;

import com.exam.examapp.dto.request.information.TermRequest;
import com.exam.examapp.model.information.Term;

public class TermMapper {
    public static Term requestTo(TermRequest termRequest) {
        return Term.builder()
                .name(termRequest.termName())
                .description(termRequest.description())
                .role(termRequest.role())
                .build();
    }
}
