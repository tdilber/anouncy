package com.beyt.anouncy.user.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientAuthorizationException extends RuntimeException {
    private List<String> errorMessageKeyList;

    public ClientAuthorizationException(String... errorMessageKey) {
        super(String.join(", ", errorMessageKey));
        errorMessageKeyList = Arrays.stream(errorMessageKey).toList();
    }

    public ClientAuthorizationException(List<String> errorMessageKey) {
        super(String.join(", ", errorMessageKey));
        errorMessageKeyList = errorMessageKey;
    }

    public ClientAuthorizationException(String errorMessageKey) {
        super(errorMessageKey);
        errorMessageKeyList = Collections.singletonList(errorMessageKey);
    }

    public ClientAuthorizationException(String errorMessageKey, Throwable cause) {
        super(errorMessageKey, cause);
        errorMessageKeyList = Collections.singletonList(errorMessageKey);
    }

    public List<String> getErrorMessageKeyList() {
        return errorMessageKeyList;
    }
}
