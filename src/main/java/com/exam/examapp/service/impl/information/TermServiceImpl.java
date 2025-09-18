package com.exam.examapp.service.impl.information;

import com.exam.examapp.AppMessage;
import com.exam.examapp.dto.request.information.TermRequest;
import com.exam.examapp.dto.request.information.TermUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.TermMapper;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.information.Term;
import com.exam.examapp.repository.information.TermRepository;
import com.exam.examapp.service.interfaces.information.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final TermRepository termRepository;

    @Override
    public String createTerm(TermRequest request) {
        if (termRepository.getTermByName(request.termName()).isPresent())
            throw new BadRequestException("Term with name " + request.termName() + " already exists.");

        if (Role.ADMIN.equals(request.role()))
            throw new BadRequestException("Admin cannot be created as term.");

        if (Role.EMPTY.equals(request.role()))
            throw new BadRequestException("Role cannot be empty.");

        Term term = TermMapper.requestTo(request);
        termRepository.save(term);
        return AppMessage.TERM_SAVE_SUCCESS.getMessage();
    }

    @Override
    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    @Override
    public Term getTermByName(String termName) {
        return termRepository.getTermByName(termName).orElseThrow(() ->
                new ResourceNotFoundException(AppMessage.TERM_NOT_FOUND.getMessage()));
    }

    @Override
    public String updateTerm(TermUpdateRequest request) {
        Optional<Term> termByName = termRepository.getTermByName(request.termName());

        Term term = termRepository.findById(request.id()).orElseThrow(() ->
                new ResourceNotFoundException(AppMessage.TERM_NOT_FOUND.getMessage()));

        if (termByName.isPresent() && !termByName.get().getId().equals(request.id()))
            throw new BadRequestException("Term with name " + request.termName() + " already exists.");

        if (Role.ADMIN.equals(request.role()))
            throw new BadRequestException("Admin cannot be updated as term.");

        if (Role.EMPTY.equals(request.role()))
            throw new BadRequestException("Role cannot be empty.");

        term.setName(request.termName());
        term.setDescription(request.description());
        termRepository.save(term);
        return AppMessage.TERM_UPDATE_SUCCESS.getMessage();
    }

    @Override
    public String deleteTerm(UUID id) {
        if (!termRepository.existsById(id))
            return AppMessage.TERM_NOT_FOUND.getMessage();
        termRepository.deleteById(id);
        return AppMessage.TERM_DELETE_SUCCESS.getMessage();
    }
}
