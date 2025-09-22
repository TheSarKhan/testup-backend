package com.exam.examapp.dto.response;

import com.exam.examapp.model.Pack;
import com.exam.examapp.model.TeacherInfo;

public record TeacherInfoResponse(TeacherInfo teacherInfo,
                                  Pack pack) {
}
