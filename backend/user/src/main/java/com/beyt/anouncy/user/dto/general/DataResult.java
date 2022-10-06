package com.beyt.anouncy.user.dto.general;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DataResult<T> implements Serializable {
    private T data;
}
