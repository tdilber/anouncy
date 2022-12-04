package com.beyt.anouncy.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "configuration")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Configuration extends BaseUuidEntity {

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "conf_key", length = 100, unique = true, nullable = false)
    private String key;

    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "conf_value", length = 2000, nullable = false)
    private String value;
}
