package com.auth.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException{

    private final String message;
    private final HttpStatus status;

    public BadRequestException(String message) {
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
