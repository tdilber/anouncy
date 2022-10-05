package com.beyt.anouncy.user.dto;

import com.beyt.anouncy.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJwtModel implements Serializable {
    private UUID userId;
    private String name;
    private String surname;
    private String username;
    private String userSessionId;
    private Date createAt;

    public UserJwtModel(User user, String sessionId) {
        this.userId = user.getId();
        this.name = user.getFirstName();
        this.surname = user.getLastName();
        this.username = user.getUsername();
        this.userSessionId = sessionId;
        this.createAt = new Date();
    }

    @JsonIgnore
    public String getFullName() {
        return name + " " + surname;
    }
}
