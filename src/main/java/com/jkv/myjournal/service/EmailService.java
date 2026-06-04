package com.jkv.myjournal.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
