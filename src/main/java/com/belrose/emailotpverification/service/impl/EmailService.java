package com.belrose.emailotpverification.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to,String subject,String body) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =new MimeMessageHelper(message,true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body,true);
            javaMailSender.send(message);
            log.info(String.format("JavaMailSender:sendEmail: %s {} is successfully sent",message));
        } catch (MessagingException e) {
            log.error(String.format("JavaMailSender:sendEmail:Error to send OTP to %s. Cause: %s",to,e));
            throw new RuntimeException(e);
        }
    }
}
