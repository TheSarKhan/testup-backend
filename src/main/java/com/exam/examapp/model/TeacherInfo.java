package com.exam.examapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class TeacherInfo {
    @JsonProperty("this_month_start_time")
    private Instant thisMonthStartTime;

    @JsonProperty("this_month_created_exam_count")
    private int thisMonthCreatedExamCount;

    @JsonProperty("currently_total_exam_count")
    private int currentlyTotalExamCount;

    @JsonProperty("exam_to_student_count_map")
    private Map<UUID, Integer> ExamToStudentCountMap;
}
