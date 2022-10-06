package com.beyt.anouncy.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
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
