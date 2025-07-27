package com.se.idoctor.exception;

public class ChatRequestAlreadyExistsException extends RuntimeException {

    public ChatRequestAlreadyExistsException(String doctorUsername, String userUsername) {
        super("Chat Request for the doctor with the username " + doctorUsername + " and for the user with username " + userUsername + " already exists.");
    }
}
