package com.beyt.anouncy.user.service;

import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.exception.ClientAuthorizationException;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.model.UserJwtModel;
import com.beyt.anouncy.user.dto.UserResolveResultDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.entity.AnonymousUser;
import com.beyt.anouncy.user.entity.User;
import com.beyt.anouncy.user.helper.HashHelper;
import com.beyt.anouncy.user.repository.AnonymousUserRepository;
import com.beyt.anouncy.user.repository.AnonymousUserSessionRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private final AnonymousUserRepository anonymousUserRepository;
    private final AnonymousUserSessionRepository anonymousUserSessionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashHelper hashHelper;

    @Autowired
    private UserContext userContext;

    @Transactional
    public UserJwtResponse signIn(UserSignInDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            throw new ClientErrorException("user.not.found");
        }

        User user = userOpt.get();
        if (!user.isActivated()) {
            throw new ClientErrorException("user.not.activated");
        }

        if (!hashHelper.check(HashHelper.HashType.USER, dto.getPassword(), user.getPassword())) {
            throw new ClientErrorException("user.password.not.correct");
        }

        String hash = hashHelper.hash(HashHelper.HashType.ANONYMOUS_USER, dto.getPassword() + "_" + user.getId());
        Optional<AnonymousUser> anonymousUserOpt = anonymousUserRepository.findByPassword(hash);

        AnonymousUser anonymousUser = anonymousUserOpt.orElseThrow(IllegalStateException::new);

        return createUserJwtResponse(user, anonymousUser.getId());
    }

    UserJwtResponse createUserJwtResponse(User user, UUID anonymousUserId) {
        String newSessionId = userSessionService.createNewSession(anonymousUserId);
        UserJwtModel userJwtModel = user.createJwtModel(newSessionId);
        return new UserJwtResponse(jwtTokenProvider.createToken(userJwtModel));
    }

    @Transactional
    public void signUp(UserSignUpDTO dto) {
        userRepository
                .findByUsername(dto.getUsername().toLowerCase())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new ClientErrorException("user.username.already.used");
                            }
                        }
                );
        userRepository
                .findOneByEmailIgnoreCase(dto.getEmail())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new ClientErrorException("user.email.already.used");
                            }
                        }
                );
        User newUser = new User();
        String encryptedPassword = hashHelper.hash(HashHelper.HashType.USER, dto.getPassword());
        newUser.setUsername(dto.getUsername().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        if (dto.getEmail() != null) {
            newUser.setEmail(dto.getEmail().toLowerCase());
        }
        newUser.setImageUrl(dto.getImageUrl());
        newUser.setLangKey(dto.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(UUID.randomUUID().toString());
        newUser.setActivationDate(Instant.now());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);

        String hashForAnonymousUser = hashHelper.hash(HashHelper.HashType.ANONYMOUS_USER, dto.getPassword() + "_" + newUser.getId()); // Because when 2 member use same password then it will not be conflicted
        AnonymousUser anonymousUser = new AnonymousUser(hashForAnonymousUser);
        anonymousUserRepository.save(anonymousUser);
    }

    @Transactional
    public UserResolveResultDTO resolveToken(String token) {
        try {
            Pair<String, UserJwtModel> tokenModel = jwtTokenProvider.getTokenModel(token);
            UserJwtModel model = tokenModel.getSecond();
            String jwtModelStr = tokenModel.getFirst();
            UUID anonymousUserId = userSessionService.findAnonymousUserIdToSessionId(model.getUserSessionId());
            String newToken = null;
            if (model.isExpired(3600)) { // one hour
                newToken = refreshSessionAndToken(model, anonymousUserId);
            }
            return new UserResolveResultDTO(model.getUserId(), anonymousUserId, jwtModelStr, newToken);
        } catch (Exception e) {
            throw new ClientAuthorizationException("need.reauthorization", e);
        }
    }

    private String refreshSessionAndToken(UserJwtModel model, UUID anonymousUserId) {
        userSessionService.deleteSession(model.getUserSessionId());
        Optional<User> userOptional = userRepository.findById(model.getUserId());
        User user = userOptional.orElseThrow(() -> new ClientAuthorizationException("need.reauthorization", new Exception()));
        UserJwtResponse userJwtResponse = createUserJwtResponse(user, anonymousUserId);
        return userJwtResponse.getToken();
    }

    @NeedLogin
    public void signOut(String token) {
        UserJwtModel model = jwtTokenProvider.getTokenModel(token).getSecond();
        userSessionService.deleteSession(model.getUserSessionId());
    }

    @Transactional
    public void activateAccount(String activationCode) {
        Optional<User> userOpt = activateRegistration(activationCode);
        User user = userOpt.orElseThrow(() -> new ClientErrorException("user.not.found"));
        userRepository.save(user);
    }

    private Optional<User> activateRegistration(String key) {
        return userRepository
                .findOneByActivationKey(key)
                .map(
                        user -> {
                            user.setActivated(true);
                            user.setActivationKey(null);
                            user.setActivationDate(null);
                            return user;
                        }
                );
    }

    public Optional<User> completePasswordReset(String newPassword, String sessionId, String key) {
        return userRepository
                .findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
                .map(
                        user -> {
                            user.setPassword(hashHelper.hash(HashHelper.HashType.USER, newPassword));
                            user.setResetKey(null);
                            user.setResetDate(null);


                            // TODO

                            return user;
                        }
                );
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
                .findOneByEmailIgnoreCase(mail)
                .filter(User::isActivated)
                .map(
                        user -> {
                            user.setResetKey(UUID.randomUUID().toString());
                            user.setResetDate(Instant.now());
                            return user;
                        }
                );
    }


    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        return true;
    }

    @NeedLogin
    public void deleteUser() {
        userRepository.findById(userContext.getUserId()).ifPresent(userRepository::delete);
        anonymousUserRepository.findById(userContext.getAnonymousUserId()).ifPresent(anonymousUserRepository::delete);
    }

    @NeedLogin
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        userRepository.findById(userContext.getUserId())
                .ifPresent(
                        user -> {
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            if (email != null) {
                                user.setEmail(email.toLowerCase());
                            }
                            user.setLangKey(langKey);
                            user.setImageUrl(imageUrl);
                        }
                );
    }

    @NeedLogin
    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        userRepository.findById(userContext.getUserId())
                .ifPresent(
                        user -> {
                            String currentEncryptedPassword = user.getPassword();

                            if (!hashHelper.check(HashHelper.HashType.USER, currentEncryptedPassword, user.getPassword())) {
                                throw new ClientErrorException("user.password.not.correct");
                            }

                            String encryptedPassword = hashHelper.hash(HashHelper.HashType.USER, newPassword);
                            user.setPassword(encryptedPassword);
                            userRepository.save(user);

                            anonymousUserRepository.findById(userContext.getAnonymousUserId())
                                    .ifPresent(anonymousUser -> {
                                        String encryptedAnonymousPassword = hashHelper.hash(HashHelper.HashType.ANONYMOUS_USER, newPassword);
                                        anonymousUser.setPassword(encryptedAnonymousPassword);
                                        anonymousUserRepository.save(anonymousUser);
                                    });
                        }
                );
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndActivationDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
                .forEach(
                        user -> {
                            log.debug("Deleting not activated user {}", user.getUsername());
                            userRepository.delete(user);
                        }
                );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJwtResponse {
        private String token;
    }
}
