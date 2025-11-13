package com.exam.examapp.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AnswerStatus {
    CORRECT, WRONG, NOT_ANSWERED, WAITING_FOR_REVIEW, HALF_CORRECT, QUARTER_CORRECT, THREE_QUARTERS_CORRECT;

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
