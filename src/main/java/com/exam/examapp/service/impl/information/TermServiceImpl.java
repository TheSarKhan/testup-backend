package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.TermRequest;
import com.exam.examapp.dto.request.information.TermUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.TermMapper;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.information.Term;
import com.exam.examapp.repository.information.TermRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.information.TermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final UserService userService;

    private final TermRepository termRepository;

    private final LogService logService;

    @Override
    public String createTerm(TermRequest request) {
        log.info("Qanun yaradılır");
        if (termRepository.getTermByName(request.termName()).isPresent())
            throw new BadRequestException(request.termName() + " adlı qanun artıq mövcuddur.");

        if (Role.ADMIN.equals(request.role()))
            throw new BadRequestException("Admin üçün qanun yaradıla bilməz.");

        if (Role.EMPTY.equals(request.role()))
            throw new BadRequestException("Rol boş ola bilməz.");

        Term term = TermMapper.requestTo(request);
        termRepository.save(term);
        log.info("Qanun yaradıldı");
        logService.save("Qanun yaradıldı", userService.getCurrentUserOrNull());
        return "Qanun yaradıldı";
    }

    @Override
    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    @Override
    public Term getTermByName(String termName) {
        return termRepository.getTermByName(termName).orElseThrow(() ->
                new ResourceNotFoundException("Qanun tapılmadı"));
    }

    @Override
    public String updateTerm(TermUpdateRequest request) {
        log.info("Qanun yenilənir");
        Optional<Term> termByName = termRepository.getTermByName(request.termName());

        Term term = termRepository.findById(request.id()).orElseThrow(() ->
                new ResourceNotFoundException("Qanun tapılmadı"));

        if (termByName.isPresent() && !termByName.get().getId().equals(request.id()))
            throw new BadRequestException(request.termName() + " adlı qanun artıq mövcuddur.");

        if (Role.ADMIN.equals(request.role()))
            throw new BadRequestException("Admin üçün qanun yaradıla bilməz.");

        if (Role.EMPTY.equals(request.role()))
            throw new BadRequestException("Rol boş ola bilməz.");

        term.setName(request.termName());
        term.setDescription(request.description());
        termRepository.save(term);
        log.info("Qanun yeniləndi");
        logService.save("Qanun yeniləndi", userService.getCurrentUserOrNull());
        return "Qanun yenilənir";
    }

    @Override
    public String deleteTerm(UUID id) {
        log.info("Qanun silinir");
        termRepository.deleteById(id);
        log.info("Qanun silindi");
        logService.save("Qanun silindi", userService.getCurrentUserOrNull());
        return "Qanun silindi";
    }
}
