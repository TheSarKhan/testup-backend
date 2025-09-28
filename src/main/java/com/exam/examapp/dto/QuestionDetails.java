package com.exam.examapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record QuestionDetails(
        @JsonProperty("variant_to_content_map")
        Map<Character, String> variantToContentMap,

        @JsonProperty("variant_to_is_picture_map")
        Map<Character, Boolean> variantToIsPictureMap,

        @JsonProperty("variant_to_has_math_content_map")
        Map<Character, Boolean> variantToHasMathContentMap,

        @JsonProperty("correct_variants")
        List<Character> correctVariants,

        @JsonProperty("number_to_content_map")
        Map<Character, String> numberToContentMap,

        @JsonProperty("number_to_is_picture_map")
        Map<Character, Boolean> numberToIsPictureMap,

        @JsonProperty("number_to_has_math_content_map")
        Map<Character, Boolean> numberToHasMathContentMap,

        @JsonProperty("number_to_correct_variants_map")
        Map<Character, List<Character>> numberToCorrectVariantsMap,

        @JsonProperty("is_auto")
        Boolean isAuto,

        @JsonProperty("listening_time")
        int listeningTime,

        String answer
) {
}