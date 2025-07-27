package com.se.idoctor.service;

import com.se.idoctor.dto.EmailBody;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmail(EmailBody emailBody, String decision) throws MessagingException, UnsupportedEncodingException;
    void sendForgotPasswordEmail(String email) throws MessagingException, UnsupportedEncodingException;
}
