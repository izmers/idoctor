package com.se.idoctor.web;

import com.se.idoctor.dto.EmailBody;
import com.se.idoctor.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {
    private EmailService emailService;

    @PostMapping("/send/{decision}")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailBody emailBody, @PathVariable String decision) throws MessagingException, UnsupportedEncodingException {
        this.emailService.sendEmail(emailBody, decision);
        return new ResponseEntity<>("Mail successfully sent", HttpStatus.OK);
    }

    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> userForgotPassword(@PathVariable String email) throws UnsupportedEncodingException, MessagingException {
        this.emailService.sendForgotPasswordEmail(email);

        return ResponseEntity.ok("Password reset email sent!");
    }
}
