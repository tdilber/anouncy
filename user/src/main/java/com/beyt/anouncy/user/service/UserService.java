package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.dto.UserAuthenticateDTO;
import com.beyt.anouncy.user.repository.AnonymousUserRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AnonymousUserRepository anonymousUserRepository;
    private final PasswordEncoder passwordEncoder;

    public void authenticate(@Valid UserAuthenticateDTO userAuthenticateDTO) {
        userRepository.findAll();
    }
}
