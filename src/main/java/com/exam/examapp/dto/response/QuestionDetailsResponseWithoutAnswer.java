package com.exam.examapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record QuestionDetailsResponseWithoutAnswer(
        @JsonProperty("variant_to_content_map")
        Map<Character, String> variantToContentMap,

        @JsonProperty("variant_to_is_picture_map")
        Map<Character, Boolean> variantToIsPictureMap,

        @JsonProperty("variant_to_has_math_content_map")
        Map<Character, Boolean> variantToHasMathContentMap,

        @JsonProperty("number_to_content_map")
        Map<Character, String> numberToContentMap,

        @JsonProperty("number_to_is_picture_map")
        Map<Character, Boolean> numberToIsPictureMap,

        @JsonProperty("number_to_has_math_content_map")
        Map<Character, Boolean> numberToHasMathContentMap,

        @JsonProperty("is_auto")
        Boolean isAuto,

        @JsonProperty("listening_time")
        int listeningTime
) {
}
