package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.context.UserContext;
import com.beyt.anouncy.user.dto.UserResolveResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserContext userContext;

    @GetMapping("/token-resolver/{token}")
    public ResponseEntity<UserResolveResultDTO> tokenResolver(@PathVariable String token) {
        log.info("Token Received : {}", token);
        return ResponseEntity.ok(new UserResolveResultDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
    }

    @GetMapping("/test")
    public ResponseEntity<Boolean> test() {
        log.info("Test Received User Id : {} Anonymous User Id : {}", userContext.getUserId(), userContext.getAnonymousUserId());
        return ResponseEntity.ok(true);
    }
}
