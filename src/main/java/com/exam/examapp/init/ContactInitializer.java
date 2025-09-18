package com.exam.examapp.init;

import com.exam.examapp.model.information.Contact;
import com.exam.examapp.repository.information.ContactRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactInitializer {
    private final ContactRepository contactRepository;

    @PostConstruct
    public void init() {
        if (contactRepository.count() == 0) {
            Contact contact = Contact.builder()
                    .title("Contact")
                    .description("Description")
                    .phone("+994 55 777 88 99")
                    .email("admin1234@gmail.com")
                    .build();
            contactRepository.save(contact);
        }
    }
}
