package com.eduardocastro.order_service.domain.exception;

public class InvalidCartDataException extends RuntimeException {
    public InvalidCartDataException(String message) {
        super(message);
    }
}
