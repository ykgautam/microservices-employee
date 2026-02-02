package com.employee.exception;

import org.springframework.http.HttpStatus;

public class CustomeException extends RuntimeException{

    private HttpStatus status;

    public CustomeException(String message, HttpStatus status){
        super(message);
        this.status=status;
    }

    public CustomeException(String message){
        super(message);
        this.status=HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
