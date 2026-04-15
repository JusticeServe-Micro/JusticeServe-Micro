package com.justiceserve.citizenservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String m) {
        super(m);
    }
}
