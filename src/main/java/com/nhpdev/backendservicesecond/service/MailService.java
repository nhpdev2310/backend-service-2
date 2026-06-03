package com.nhpdev.backendservicesecond.service;

public interface MailService {
    void sendVerificationEmail(String to, String subject, String displayName, String templateName, String verificationLink);
}
