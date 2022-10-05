package com.beyt.anouncy.user.service;

import com.beyt.anouncy.user.entity.AnonymousUserSession;
import com.beyt.anouncy.user.exception.ClientErrorException;
import com.beyt.anouncy.user.helper.HashHelper;
import com.beyt.anouncy.user.repository.AnonymousUserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final AnonymousUserSessionRepository anonymousUserSessionRepository;
    private final HashHelper hashHelper;

    public UUID findAnonymousUserIdToSessionId(String sessionId) {
        String hash = hashHelper.hash(HashHelper.HashType.SESSION, sessionId);

        return anonymousUserSessionRepository.findBySessionHash(hash).map(AnonymousUserSession::getAnonymousUserId)
                .orElseThrow(() -> new ClientErrorException("session.not.found"));
    }

    public String createNewSession(UUID anonymousUserId) {
        String sessionId = UUID.randomUUID().toString();
        String hash = hashHelper.hash(HashHelper.HashType.SESSION, sessionId);
        AnonymousUserSession anonymousUserSession = new AnonymousUserSession(hash, anonymousUserId);
        anonymousUserSessionRepository.save(anonymousUserSession);
        return sessionId;
    }

    @Transactional
    public void deleteSession(String sessionId) {
        String hash = hashHelper.hash(HashHelper.HashType.SESSION, sessionId);
        anonymousUserSessionRepository.deleteBySessionHash(hash);
    }
}
