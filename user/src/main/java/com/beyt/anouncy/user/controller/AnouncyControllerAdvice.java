package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.dto.ApiErrorDTO;
import com.beyt.anouncy.user.exception.ClientErrorException;
import com.beyt.anouncy.user.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class AnouncyControllerAdvice {
    @Autowired
    private MessageService messageService;

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ApiErrorDTO> handleClientErrorException(ClientErrorException exception) {
        String errorMessages = exception.getErrorMessageKeyList()
                .stream()
                .map(errorCode -> messageService.getMessage(errorCode))
                .collect(Collectors.joining("\n"));

        return new ResponseEntity<>(new ApiErrorDTO(errorMessages, String.join("\n", exception.getErrorMessageKeyList()), HttpStatus.BAD_REQUEST.value() + ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorDTO> handleException(Throwable exception) {
        return new ResponseEntity<>(new ApiErrorDTO(messageService.getMessage("internal.server.error"), "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR.value() + ""), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
