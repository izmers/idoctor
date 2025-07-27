package com.se.idoctor.exception;

public class UserIsNotDoctorException extends RuntimeException {

    public UserIsNotDoctorException(Long userId) {
        super("User with the id " + userId + " is not a doctor.");
    }
}
