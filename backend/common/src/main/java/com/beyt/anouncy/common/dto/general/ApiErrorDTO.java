package com.beyt.anouncy.common.dto.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDTO {
    private Object message;
    private String code;
    private String httpStatus;
    private Date timestamp = new Date();

    public ApiErrorDTO(Object message, String code, String httpStatus) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
