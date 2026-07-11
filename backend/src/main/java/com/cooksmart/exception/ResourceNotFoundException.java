package com.cooksmart.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CookSmartException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
