package com.beyt.anouncy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResolveResultDTO {
    private String userId;
    private String anonymousUserId;
}
