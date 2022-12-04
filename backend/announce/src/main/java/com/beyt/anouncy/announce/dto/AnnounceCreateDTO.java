package com.beyt.anouncy.announce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceCreateDTO implements Serializable {

    @NotBlank
    @Length(min = 3, max = 200) // TODO think about it
    private String body;
}
