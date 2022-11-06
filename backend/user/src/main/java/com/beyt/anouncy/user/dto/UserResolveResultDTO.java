package com.beyt.anouncy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResolveResultDTO {
    private UUID userId;
    private UUID anonymousUserId;
    private String newToken;
}
