package com.beyt.anouncy.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousUserSession implements Serializable {
    @Id
    @Column(name = "session_hash", nullable = false, unique = true)
    private String sessionHash;

    @NotNull
    @Column(name = "anonymous_user_id", nullable = false)
    private UUID anonymousUserId;
}
