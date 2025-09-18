package com.exam.examapp.mapper.information;

import com.exam.examapp.dto.request.information.ContactUpdateRequest;
import com.exam.examapp.model.information.Contact;
import java.util.HashMap;
import java.util.Map;

public class ContactMapper {
    public static Contact updateRequestTo(Contact oldContact,
                                          ContactUpdateRequest request) {
        oldContact.setTitle(request.title());
        oldContact.setDescription(request.description());
        Map<String, String> nameRedirectUrl = new HashMap<>();
        for (int i = 0; i < request.contactNames().size(); i++) {
            nameRedirectUrl.put(request.contactNames().get(i), request.contactRedirectUrls().get(i));
        }
        oldContact.setNameToRedirectUrl(nameRedirectUrl);
        return oldContact;
    }
}
