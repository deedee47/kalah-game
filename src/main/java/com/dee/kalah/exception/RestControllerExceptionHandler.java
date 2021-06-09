package com.dee.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(KalahInvalidMoveException.class)
    public ResponseEntity<Error> kalahInvalidMoveException(KalahInvalidMoveException ex, WebRequest request) {
        Error message = new Error(
                                    HttpStatus.BAD_REQUEST.value(),
                                    new Date(),
                                    ex.getMessage(),
                                    request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Error> databaseException(DatabaseException ex, WebRequest request) {
        Error message = new Error(
                                HttpStatus.NOT_FOUND.value(),
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> allExceptionHandler(Exception ex, WebRequest request) {
        Error message = new Error(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
