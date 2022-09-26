package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.dto.UserAuthenticateDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.dto.UserSignOutDTO;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.entity.User;
import com.beyt.anouncy.user.exception.ClientErrorException;
import com.beyt.anouncy.user.helper.HashHelper;
import com.beyt.anouncy.user.repository.AnonymousUserRepository;
import com.beyt.anouncy.user.repository.AnonymousUserSessionRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AnonymousUserRepository anonymousUserRepository;
    private final AnonymousUserSessionRepository anonymousUserSessionRepository;
    private final HashHelper hashHelper;

    public void authenticate(@Valid UserAuthenticateDTO userAuthenticateDTO) {
        userRepository.findAll();
    }

    public void signIn(UserSignInDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            throw new ClientErrorException("user.not.found");
        }

        User user = userOpt.get();
        if (!hashHelper.check("anouncy.password.salt.user", dto.getPassword(), user.getPassword())) {
            throw new ClientErrorException("user.password.not.correct");
        }
    }

    public void signUp(UserSignUpDTO dto) {

    }

    public void signOut(UserSignOutDTO dto) {

    }
}
