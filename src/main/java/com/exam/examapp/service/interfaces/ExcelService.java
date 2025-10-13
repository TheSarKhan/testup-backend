package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.response.UsersForAdminResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ExcelService {
    ByteArrayInputStream exportUsers(List<UsersForAdminResponse> students) throws IOException;
}
