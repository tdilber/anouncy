package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.dto.UserAuthenticateDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.entity.User;
import com.beyt.anouncy.user.exception.ClientErrorException;
import com.beyt.anouncy.user.repository.AnonymousUserRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AnonymousUserRepository anonymousUserRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PasswordEncoder sCryptPasswordEncoder;

    @Value("${anouncy.password.salt.user}")
    private String userSalt;

    @Value("${anouncy.password.salt.anonymous}")
    private String anonymousUserSalt;

    public void authenticate(@Valid UserAuthenticateDTO userAuthenticateDTO) {
        userRepository.findAll();
    }

    public void signIn(UserSignInDTO dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        if (user.isEmpty()) {
            throw new ClientErrorException("user.not.found");
        }

    }
}
