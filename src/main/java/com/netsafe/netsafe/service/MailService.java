package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;
import jakarta.mail.MessagingException;

public interface MailService {

    Result sendMail(String send);

    Result sendMailHtml(String send);
    Result checkMail(String send);

    void test() throws MessagingException;
}
