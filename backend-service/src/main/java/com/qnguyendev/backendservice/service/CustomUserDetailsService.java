package com.qnguyendev.backendservice.service;

import com.qnguyendev.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService {

    UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return userRepository::findByUsername;
    }
}
