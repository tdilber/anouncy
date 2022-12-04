package com.beyt.anouncy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO forget password
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthenticateDTO {
    @jakarta.validation.constraints.NotNull
    private String username;
    @jakarta.validation.constraints.NotNull
    private String password;
}
