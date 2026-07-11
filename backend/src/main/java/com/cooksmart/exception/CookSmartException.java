package com.cooksmart.exception;

import org.springframework.http.HttpStatus;

public class CookSmartException extends RuntimeException {

    private final HttpStatus status;

    public CookSmartException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
