package com.test.demoproductkumamesh.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
