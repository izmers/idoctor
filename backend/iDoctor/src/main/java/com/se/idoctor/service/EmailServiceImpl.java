package com.se.idoctor.service;

import com.se.idoctor.dto.EmailBody;
import com.se.idoctor.entity.PasswordResetToken;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.repository.PasswordResetTokenRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private DoctorService doctorService;
    private UserService userService;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private static final Dotenv dotenv = Dotenv.load();

    private JavaMailSenderImpl emailSetup() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("remzi.cetin64@gmail.com");
        mailSender.setPassword(dotenv.get("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Override
    public void sendEmail(EmailBody emailBody, String decision) throws MessagingException, UnsupportedEncodingException {
        if (decision.equals("accept")) {
            this.doctorService.approveDoctorAccount(emailBody.getTo());
        }

        JavaMailSenderImpl mailSender = emailSetup();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom(new InternetAddress(emailBody.getUsername(), "iDoctor Support"));
        helper.setTo(emailBody.getTo());
        helper.setSubject(emailBody.getSubject());
        helper.setText(emailBody.getBody(), true);

        mailSender.send(mimeMessage);
    }

    public void sendForgotPasswordEmail(String userEmail) throws MessagingException, UnsupportedEncodingException {
        Userx user = this.userService.getUserByEmail(userEmail);

        PasswordResetToken passwordResetToken = user.getPasswordResetToken() != null ? user.getPasswordResetToken() : new PasswordResetToken();
        String token = UUID.randomUUID().toString();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        passwordResetToken.setUser(user);
        this.passwordResetTokenRepository.save(passwordResetToken);

        JavaMailSenderImpl mailSender = emailSetup();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMessage = "<body>" +
                "<p>Dear User,</p>" +
                "<p>Click the button below to reset your password:</p>" +
                "<a href='http://localhost:9090/api/userx/verify-reset-password/" + user.getId() + "' " +
                "style='display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007BFF; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                "<p>If you didn't request this, you can ignore this email.</p>" +
                "<p>Best regards,<br>Your iDoctor-App Team</p>" +
                "</body>" +
                "</html>";

        helper.setFrom(new InternetAddress("remzi.cetin64@gmail.com", "iDoctor Support"));
        helper.setTo(userEmail);
        helper.setSubject("Password reset request");
        helper.setText(htmlMessage, true);

        mailSender.send(mimeMessage);
    }
}
