package com.se.idoctor.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> classType, Long id) {
        super(classType.getSimpleName() + "with the id " + id + " was not found.");
    }

    public EntityNotFoundException(Class<?> type, String name) {
        super(type.getSimpleName() + " width the username or email " + name + " was not found.");
    }
}
