package com.skulkina.url_shortener_api.service;

import com.skulkina.url_shortener_api.dto.AuthResponse;
import com.skulkina.url_shortener_api.dto.RegisterRequest;
import com.skulkina.url_shortener_api.entity.Role;
import com.skulkina.url_shortener_api.entity.User;
import com.skulkina.url_shortener_api.exception.EmailAlreadyExistsException;
import com.skulkina.url_shortener_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new AuthResponse("temporary-token");
    }

}
