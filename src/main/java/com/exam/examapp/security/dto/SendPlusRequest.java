package com.exam.examapp.security.dto;

import java.util.List;

public record SendPlusRequest(
        Email email
) {

    public record Email(
            String html,
            String text,
            String subject,
            From from,
            List<To> to
    ) {}

    public record From(
            String name,
            String email
    ) {}

    public record To(
            String name,
            String email
    ) {}
}
