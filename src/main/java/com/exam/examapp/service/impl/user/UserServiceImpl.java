package com.exam.examapp.service.impl.user;

import com.exam.examapp.dto.request.UserFilterRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.exception.custom.UserNotLoginException;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.UserRepository;
import com.exam.examapp.security.model.CustomUserDetails;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final LogService logService;

    private final UserSpecification userSpecification;

    @Override
    public User save(User user) {
        log.info("İstifadəçi yaradılır: {}", user.getEmail());
        User save = userRepository.save(user);
        log.info("İstifadəçi yaradıldı: {}", user.getEmail());
        logService.save("İstifadəçi yaradıldı:" + user.getEmail(), getCurrentUserOrNull());
        return save;
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("İstifadəçi tapılmadı"));
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("İstifadəçi tapılmadı"));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal()))
            throw new UserNotLoginException("İstifadəçi tapılmadı");

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        return getByEmail(principal.getUsername());
    }

    @Override
    public TeacherInfo getTeacherInfo() {
        User user = getCurrentUser();
        if (!Role.TEACHER.equals(user.getRole()))
            throw new BadRequestException("Sən müəllim deyilsən");

        return user.getInfo();
    }

    @Override
    public User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal()))
            return null;

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        return getByEmail(principal.getUsername());
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.getAllByRole(role);
    }

    @Override
    public List<String> getEmailList(UserFilterRequest request) {
        Specification<User> specification = userSpecification.filter(request);
        return userRepository.findAll(specification).stream().map(User::getEmail).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}
