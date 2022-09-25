package com.beyt.anouncy.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserSignInDTO implements Serializable {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
