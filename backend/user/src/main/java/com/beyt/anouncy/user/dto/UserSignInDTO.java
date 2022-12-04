package com.beyt.anouncy.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserSignInDTO implements Serializable {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
