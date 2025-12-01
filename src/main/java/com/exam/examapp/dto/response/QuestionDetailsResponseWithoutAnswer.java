package com.exam.examapp.dto.response;

import java.util.Map;

public record QuestionDetailsResponseWithoutAnswer(
        Map<Character, String> variantToContentMap,

        Map<Character, Boolean> variantToIsPictureMap,

        Map<Character, Boolean> variantToHasMathContentMap,

        Map<Character, String> numberToContentMap,

        Map<Character, Boolean> numberToIsPictureMap,

        Map<Character, Boolean> numberToHasMathContentMap,

        Boolean isAuto,

        int listeningTime
) {
}
