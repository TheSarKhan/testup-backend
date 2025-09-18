package com.exam.examapp.service.interfaces.information;

import com.exam.examapp.dto.request.information.ContactUpdateRequest;
import com.exam.examapp.model.information.Contact;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContactService {
    void updateContact(ContactUpdateRequest request, List<MultipartFile> icons);

    Contact getContact();
}
