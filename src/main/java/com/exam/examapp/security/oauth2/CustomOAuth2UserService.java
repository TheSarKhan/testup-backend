package com.exam.examapp.security.oauth2;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String googleId = oidcUser.getAttribute("sub");

        Optional<User> byEmail = userRepository.findByEmail(email);

        User user;

        if (byEmail.isPresent()) {
            user = byEmail.get();
            user.setGoogleId(googleId);
            userRepository.save(user);
        } else {
            user = User.builder()
                    .email(email)
                    .fullName(name)
                    .googleId(googleId)
                    .role(Role.EMPTY)
                    .isAcceptedTerms(true)
                    .build();
            userRepository.save(user);
        }

        return new CustomOidcUser(
                oidcUser,
                user.getId(),
                name,
                email,
                user.getRole().name(),
                user.getPack());
    }
}