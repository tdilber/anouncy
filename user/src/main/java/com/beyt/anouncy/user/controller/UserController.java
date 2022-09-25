package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.context.UserContext;
import com.beyt.anouncy.user.dto.UserResolveResultDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private UserContext userContext;


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid UserSignInDTO dto) {
        userService.signIn(dto);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/token-resolver/{token}")
    public ResponseEntity<UserResolveResultDTO> tokenResolver(@PathVariable String token) {
        log.info("Token Received : {}", token);
        return ResponseEntity.ok(new UserResolveResultDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
    }
}

