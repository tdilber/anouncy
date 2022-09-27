package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.context.UserContext;
import com.beyt.anouncy.user.dto.UserJwtModel;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.dto.UserSignOutDTO;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.entity.AnonymousUser;
import com.beyt.anouncy.user.entity.AnonymousUserSession;
import com.beyt.anouncy.user.entity.User;
import com.beyt.anouncy.user.exception.ClientErrorException;
import com.beyt.anouncy.user.helper.HashHelper;
import com.beyt.anouncy.user.repository.AnonymousUserRepository;
import com.beyt.anouncy.user.repository.AnonymousUserSessionRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AnonymousUserRepository anonymousUserRepository;
    private final AnonymousUserSessionRepository anonymousUserSessionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashHelper hashHelper;

    @Autowired
    private UserContext userContext;

    @Transactional
    public String signIn(UserSignInDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            throw new ClientErrorException("user.not.found");
        }

        User user = userOpt.get();
        if (!hashHelper.check("anouncy.password.salt.user", dto.getPassword(), user.getPassword())) {
            throw new ClientErrorException("user.password.not.correct");
        }

        String hash = hashHelper.hash("anouncy.password.salt.anonymous", dto.getPassword());
        Optional<AnonymousUser> anonymousUserOpt = anonymousUserRepository.findByPassword(hash);

        AnonymousUser anonymousUser = anonymousUserOpt.orElseThrow(IllegalStateException::new);

        UUID sessionId = UUID.randomUUID();
        UserJwtModel userJwtModel = new UserJwtModel(user, sessionId);

        AnonymousUserSession anonymousUserSession = new AnonymousUserSession(hashHelper.hash("anouncy.password.salt.session", sessionId.toString()), anonymousUser.getId());
        anonymousUserSessionRepository.save(anonymousUserSession);

//        userContext.getAnonymousUserId() TODO delete if session id contains

        return jwtTokenProvider.createToken(userJwtModel, true);
    }

    public void signUp(UserSignUpDTO dto) {

    }

    public void signOut(UserSignOutDTO dto) {

    }
}
