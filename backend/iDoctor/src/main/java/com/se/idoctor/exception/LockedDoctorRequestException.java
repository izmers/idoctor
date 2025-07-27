package com.se.idoctor.exception;

public class LockedDoctorRequestException extends RuntimeException {

    public LockedDoctorRequestException(Long doctorId) {
        super("Doctor with the id " + doctorId + " tries to make an not permitted request.");
    }
}
