package com.beyt.anouncy.common.exception;

// TODO in future set an alarm (with grafana or something like) for this exception.
public class DeveloperMistakeException extends IllegalStateException {
    public DeveloperMistakeException(String message) {
        super(message);
    }

    public DeveloperMistakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeveloperMistakeException(Throwable cause) {
        super(cause);
    }
}
