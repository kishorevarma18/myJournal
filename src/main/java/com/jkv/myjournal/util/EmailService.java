package com.jkv.myjournal.util;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
