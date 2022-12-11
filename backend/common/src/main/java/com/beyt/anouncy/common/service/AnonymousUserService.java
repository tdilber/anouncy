//package com.beyt.anouncy.common.service;
//
//import com.beyt.anouncy.common.context.UserContext;
//import com.beyt.anouncy.common.entity.neo4j.AnonymousUser;
//import com.beyt.anouncy.common.repository.AnonymousUserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AnonymousUserService {
//    private final AnonymousUserRepository anonymousUserRepository;
//    private final UserContext userContext;
//
//    public AnonymousUser getCurrentUser() {
//        UUID anonymousUserId = userContext.getAnonymousUserId();
//        Optional<AnonymousUser> anonymousUserOptional = anonymousUserRepository.findById(anonymousUserId);
//
//        return anonymousUserOptional.orElseGet(() -> anonymousUserRepository.save(new AnonymousUser(anonymousUserId)));
//    }
//}
