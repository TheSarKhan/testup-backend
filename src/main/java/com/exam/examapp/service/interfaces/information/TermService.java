package com.exam.examapp.service.interfaces.information;

import com.exam.examapp.dto.request.information.TermRequest;
import com.exam.examapp.dto.request.information.TermUpdateRequest;
import com.exam.examapp.model.information.Term;

import java.util.List;
import java.util.UUID;

public interface TermService {
    String createTerm(TermRequest request);

    List<Term> getAllTerms();

    Term getTermByName(String termName);

    String updateTerm(TermUpdateRequest request);

    String deleteTerm(UUID id);
}
