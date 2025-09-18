package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.ContactUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.mapper.information.ContactMapper;
import com.exam.examapp.model.enums.ContactImageType;
import com.exam.examapp.model.information.Contact;
import com.exam.examapp.repository.information.ContactRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.information.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private static final String IMAGE_PATH = "uploads/images/contacts";

    private final ContactRepository contactRepository;

    private final FileService fileService;

    @Override
    public void updateContact(ContactUpdateRequest request, List<MultipartFile> icons) {
        if (request.contactNames().size() * 2 != icons.size())
            throw new BadRequestException("Icons size not match");

        Contact contact = contactRepository.findAll().getFirst();
        Contact updatedContact = ContactMapper.updateRequestTo(contact, request);
        if (!contact.getNameToImageUrls().isEmpty()) {
            for (Map.Entry<String, Map<ContactImageType, String>> entry : contact.getNameToImageUrls().entrySet()) {
                for (Map.Entry<ContactImageType, String> entry2 : entry.getValue().entrySet()) {
                    fileService.deleteFile(IMAGE_PATH, entry2.getValue());
                }
            }
        }
        Map<String, Map<ContactImageType, String>> nameToImageUrlMap = new HashMap<>();
        for (int i = 0; i < icons.size() / 2; i++) {
            Map<ContactImageType, String> map = new HashMap<>();
            for (int j = 0; j < 2; j++) {
                map.put(ContactImageType.values()[j], IMAGE_PATH);
            }
            nameToImageUrlMap.put(request.contactNames().get(i), map);
        }
        contact.setNameToImageUrls(nameToImageUrlMap);

        contact.setPhone(request.phone());
        contact.setEmail(request.email());
        contactRepository.save(updatedContact);
    }

    @Override
    public Contact getContact() {
        return contactRepository.findAll().getFirst();
    }
}
