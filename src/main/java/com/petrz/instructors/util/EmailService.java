package com.petrz.instructors.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


/**
 * Note that the password for your account should not be an ordinary password, but an application password generated for your google account.
 * Follow this link to see the details and to generate your Google App Password: https://support.google.com/accounts/answer/185833    Sign in using App Passwords
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    /**
     * see application.properties
     */
    @Value("${spring.mail.username}")
    private String springMailUserName;

    public void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Instructors WebApp"+" <"+springMailUserName+">");
        message.setTo(to);
        message.setReplyTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}

