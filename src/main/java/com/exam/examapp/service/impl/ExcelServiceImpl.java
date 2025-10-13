package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.UsersForAdminResponse;
import com.exam.examapp.service.interfaces.ExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public ByteArrayInputStream exportUsers(List<UsersForAdminResponse> users) throws IOException {
        String[] columns = {"ID", "Full Name", "Email", "Role", "Profile Picture Url",
                "Pack Name", "Phone Number", "Is Active", "Created At"};
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (UsersForAdminResponse user : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(String.valueOf(user.id()));
            row.createCell(1).setCellValue(user.fullName());
            row.createCell(2).setCellValue(user.email());
            row.createCell(3).setCellValue(user.role().toString());
            row.createCell(4).setCellValue(user.profilePictureUrl());
            row.createCell(5).setCellValue(user.packName());
            row.createCell(6).setCellValue(user.phoneNumber());
            row.createCell(7).setCellValue(user.isActive());
            row.createCell(8).setCellValue(user.createAt().toString());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
