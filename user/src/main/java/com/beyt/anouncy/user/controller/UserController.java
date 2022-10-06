package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.dto.UserResolveResultDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/sign-up")
    public ResponseEntity<Boolean> signUp(@RequestBody @Valid UserSignUpDTO dto) {
        userService.signUp(dto);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/activate/{activationCode}")
    public ResponseEntity<Boolean> activateAccount(@PathVariable @NotBlank String activationCode) {
        userService.activateAccount(activationCode);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserService.UserJwtResponse> signIn(@RequestBody @Valid UserSignInDTO dto) {
        UserService.UserJwtResponse userJwtResponse = userService.signIn(dto);
        return ResponseEntity.ok(userJwtResponse);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Boolean> signOut(@RequestHeader("Authorization") String token) {
        userService.signOut(token);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/token-resolver/{token}")
    public ResponseEntity<UserResolveResultDTO> tokenResolver(@PathVariable @NotBlank String token) {
        UserResolveResultDTO dto = userService.resolveToken(token);
        return ResponseEntity.ok(dto);
    }
}

