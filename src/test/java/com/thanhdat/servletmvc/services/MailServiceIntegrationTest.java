package com.thanhdat.servletmvc.services;

import static org.junit.Assume.assumeTrue;

import org.junit.Test;

import com.thanhdat.servletmvc.services.impl.SmtpMailService;

public class MailServiceIntegrationTest {

    @Test
    public void shouldSendEmailThroughConfiguredSmtp() {
        String recipient =
            System.getenv("MAIL_TEST_RECIPIENT");

        assumeTrue(
            "Bỏ qua vì chưa cấu hình MAIL_TEST_RECIPIENT.",
            recipient != null && !recipient.isBlank()
        );

        MailService mailService = new SmtpMailService();

        mailService.sendTextEmail(
            recipient.trim(),
            "Servlet MVC Practice - Mailtrap SMTP Test",
            "Email này xác nhận MAIL-01 gửi SMTP thành công."
        );
    }
}