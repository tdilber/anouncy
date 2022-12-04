package com.beyt.anouncy.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "anonymus_user")
@Entity
public class AnonymousUser extends BaseUuidEntity {

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 250)
    @Column(name = "password_hash", length = 250, nullable = false)
    private String password;
}
