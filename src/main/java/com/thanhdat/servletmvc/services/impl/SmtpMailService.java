package com.thanhdat.servletmvc.services.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import com.thanhdat.servletmvc.config.MailConfig;
import com.thanhdat.servletmvc.exceptions.MailDeliveryException;
import com.thanhdat.servletmvc.services.MailService;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public final class SmtpMailService
        implements MailService {

    private final Session mailSession;
    private final String fromAddress;
    private final String fromName;

    public SmtpMailService() {
        this(
                createMailSession(),
                MailConfig.getFromAddress(),
                MailConfig.getFromName()
        );
    }

    private SmtpMailService(
            Session mailSession,
            String fromAddress,
            String fromName
    ) {
        this.mailSession = mailSession;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    @Override
    public void sendTextEmail(
            String recipient,
            String subject,
            String content
    ) {
        String normalizedRecipient = requireText(
                recipient,
                "Địa chỉ email nhận không được để trống."
        );

        String normalizedSubject = requireText(
                subject,
                "Tiêu đề email không được để trống."
        );

        String normalizedContent = requireText(
                content,
                "Nội dung email không được để trống."
        );

        try {
            InternetAddress recipientAddress =
                    new InternetAddress(
                            normalizedRecipient,
                            true
                    );

            recipientAddress.validate();

            MimeMessage message =
                    new MimeMessage(mailSession);

            message.setFrom(
                    new InternetAddress(
                            fromAddress,
                            fromName,
                            StandardCharsets.UTF_8.name()
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    recipientAddress
            );

            message.setSubject(
                    normalizedSubject,
                    StandardCharsets.UTF_8.name()
            );

            message.setText(
                    normalizedContent,
                    StandardCharsets.UTF_8.name()
            );

            message.setSentDate(new Date());

            Transport.send(message);
        } catch (
                MessagingException
                | UnsupportedEncodingException exception
        ) {
            throw new MailDeliveryException(
                    "Không thể gửi email qua SMTP.",
                    exception
            );
        }
    }

    private static Session createMailSession() {
        Properties smtpProperties =
                MailConfig.getSmtpProperties();

        boolean authenticationEnabled =
                Boolean.parseBoolean(
                        smtpProperties.getProperty(
                                "mail.smtp.auth"
                        )
                );

        if (!authenticationEnabled) {
            return Session.getInstance(smtpProperties);
        }

        String username = MailConfig.getUsername();
        String password = MailConfig.getPassword();

        Authenticator authenticator =
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                            getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                username,
                                password
                        );
                    }
                };

        return Session.getInstance(
                smtpProperties,
                authenticator
        );
    }

    private static String requireText(
            String value,
            String errorMessage
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        return value.trim();
    }
}