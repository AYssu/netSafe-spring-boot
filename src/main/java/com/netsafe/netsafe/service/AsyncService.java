package com.netsafe.netsafe.service;

import jakarta.mail.internet.MimeMessage;

import java.time.Instant;

public interface AsyncService {
    public void sendEmailAsync(Instant now, MimeMessage mimeMessage);
}
