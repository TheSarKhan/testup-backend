package com.exam.examapp.dto.response;

import java.util.Map;

public record GraphResponse(
        Map<Integer, Double> yearToMap,
        Map<String, Double> monthToMap,
        Map<String, Double> dayToMap
){
}
