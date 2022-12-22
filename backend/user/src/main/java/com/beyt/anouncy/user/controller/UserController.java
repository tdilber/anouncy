package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.dto.UserResolveResultDTO;
import com.beyt.anouncy.user.dto.UserSignInDTO;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(description = "/user/v1", name = "User Service (Select User Service)")
@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> signUp(@RequestBody @Valid UserSignUpDTO dto) {
        userService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    @GetMapping("/activate/{activationCode}")
    public ResponseEntity<String> activateAccount(@PathVariable @NotBlank String activationCode) {
        userService.activateAccount(activationCode);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserService.UserJwtResponse> signIn(@RequestBody @Valid UserSignInDTO dto) {
        UserService.UserJwtResponse userJwtResponse = userService.signIn(dto);
        return ResponseEntity.ok(userJwtResponse);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> signOut(@RequestHeader("Authorization") String token) {
        userService.signOut(token);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/token-resolver/{token}")
    public ResponseEntity<UserResolveResultDTO> tokenResolver(@PathVariable @NotBlank String token) {
        UserResolveResultDTO dto = userService.resolveToken(token);
        return ResponseEntity.ok(dto);
    }
}

