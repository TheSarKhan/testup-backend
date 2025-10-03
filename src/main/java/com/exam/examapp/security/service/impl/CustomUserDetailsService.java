package com.exam.examapp.security.service.impl;

import com.exam.examapp.model.User;
import com.exam.examapp.security.model.CustomUserDetails;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getCustomUserDetails(userService.getByEmail(email));
    }

    public CustomUserDetails getCustomUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}
