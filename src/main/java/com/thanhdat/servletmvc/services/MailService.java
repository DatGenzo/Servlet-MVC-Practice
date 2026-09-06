package com.thanhdat.servletmvc.services;

public interface MailService {

    void sendTextEmail(
            String recipient,
            String subject,
            String content
    );
}