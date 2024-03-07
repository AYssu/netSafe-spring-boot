package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;
import jakarta.mail.MessagingException;

public interface MailService {


    Result sendMail(String send) throws MessagingException;

    Result checkMail(String send);

}
