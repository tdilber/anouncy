package com.beyt.anouncy.common.dto.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ResponseDataEntity<T> extends ResponseEntity<DataResult<T>> {

    public ResponseDataEntity(HttpStatus status) {
        super(status);
    }

    public ResponseDataEntity(T body, HttpStatus status) {
        super(new DataResult<>(body), status);
    }

    public ResponseDataEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    public ResponseDataEntity(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(new DataResult<>(body), headers, status);
    }

    public ResponseDataEntity(T body, MultiValueMap<String, String> headers, int rawStatus) {
        super(new DataResult<>(body), headers, rawStatus);
    }

//    public static <T> ResponseEntity<T> ok(@Nullable T body) {
//        return ok().body(new DataResult<T>(body));
//    }

}
