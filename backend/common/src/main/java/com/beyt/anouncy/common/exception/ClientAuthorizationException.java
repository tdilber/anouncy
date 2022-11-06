package com.beyt.anouncy.common.exception;

import java.util.List;

public class ClientAuthorizationException extends RuntimeException {
    private List<String> errorMessageKeyList;

    public ClientAuthorizationException(String errorMessageKey) {
        super(errorMessageKey);
        errorMessageKeyList = List.of(errorMessageKey);
    }

    public ClientAuthorizationException(String errorMessageKey, Throwable cause) {
        super(errorMessageKey, cause);
        errorMessageKeyList = List.of(errorMessageKey);
    }

    public List<String> getErrorMessageKeyList() {
        return errorMessageKeyList;
    }
}
