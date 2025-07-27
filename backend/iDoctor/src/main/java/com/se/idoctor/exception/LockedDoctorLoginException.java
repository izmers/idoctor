package com.se.idoctor.exception;

public class LockedDoctorLoginException extends RuntimeException {

    public LockedDoctorLoginException(Long doctorId) {
        super("Doctor with the id " + doctorId + " tries to login with a locked account.");
    }
}
