package com.beyt.anouncy.common.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientErrorException extends RuntimeException {
    private List<String> errorMessageKeyList;

    public ClientErrorException(String... errorMessageKey) {
        super(String.join(", ", errorMessageKey));
        errorMessageKeyList = Arrays.stream(errorMessageKey).toList();
    }

    public ClientErrorException(List<String> errorMessageKey) {
        super(String.join(", ", errorMessageKey));
        errorMessageKeyList = errorMessageKey;
    }

    public ClientErrorException(String errorMessageKey) {
        super(errorMessageKey);
        errorMessageKeyList = Collections.singletonList(errorMessageKey);
    }

    public ClientErrorException(String errorMessageKey, Throwable cause) {
        super(errorMessageKey, cause);
        errorMessageKeyList = Collections.singletonList(errorMessageKey);
    }

    public List<String> getErrorMessageKeyList() {
        return errorMessageKeyList;
    }
}
