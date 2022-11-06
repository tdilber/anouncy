package com.beyt.anouncy.common.exception;

import java.util.List;

public class ClientErrorException extends RuntimeException {
    private List<String> errorMessageKeyList;

    public ClientErrorException(String errorMessageKey) {
        super(errorMessageKey);
        errorMessageKeyList = List.of(errorMessageKey);
    }

    public ClientErrorException(String errorMessageKey, Throwable cause) {
        super(errorMessageKey, cause);
        errorMessageKeyList = List.of(errorMessageKey);
    }

    public List<String> getErrorMessageKeyList() {
        return errorMessageKeyList;
    }
}
