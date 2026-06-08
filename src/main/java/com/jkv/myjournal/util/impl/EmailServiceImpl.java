package com.jkv.myjournal.util.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jkv.myjournal.util.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender javaMailSender;

    @Async
    //using Async here so that this process can be handled by another thread, without blocking the main thread.
    @Override
    public void sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            log.info("Sending email to: {}", to);
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", to);
        }
        catch(Exception e){
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

}
