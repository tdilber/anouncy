package com.beyt.anouncy.common.controller;


import com.beyt.anouncy.common.dto.general.ApiErrorDTO;
import com.beyt.anouncy.common.exception.ClientAuthorizationException;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class AnouncyControllerAdvice {
    @Autowired
    private MessageService messageService;

    @ExceptionHandler(ClientAuthorizationException.class)
    public ResponseEntity<ApiErrorDTO> handleClientAuthorizationException(ClientAuthorizationException exception) {
        String errorMessages = exception.getErrorMessageKeyList()
                .stream()
                .map(errorCode -> messageService.getMessage(errorCode))
                .collect(Collectors.joining("\n"));
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiErrorDTO(errorMessages, String.join("\n", exception.getErrorMessageKeyList()), HttpStatus.BAD_REQUEST.value() + ""), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ApiErrorDTO> handleClientErrorException(ClientErrorException exception) {
        String errorMessages = exception.getErrorMessageKeyList()
                .stream()
                .map(errorCode -> messageService.getMessage(errorCode))
                .collect(Collectors.joining("\n"));
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiErrorDTO(errorMessages, String.join("\n", exception.getErrorMessageKeyList()), HttpStatus.BAD_REQUEST.value() + ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorDTO> handleException(Throwable exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiErrorDTO(messageService.getMessage("internal.server.error"), "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR.value() + ""), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
