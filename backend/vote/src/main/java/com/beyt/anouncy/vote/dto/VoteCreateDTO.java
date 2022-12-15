package com.beyt.anouncy.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateDTO implements Serializable {

    @NotNull
    private Boolean value;
}
