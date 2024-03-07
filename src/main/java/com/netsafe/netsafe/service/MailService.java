package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;
import jakarta.mail.MessagingException;

public interface MailService {


    Result sendMail(String send,String ip) throws MessagingException;

    Result checkMail(String send);

    Result sendMail(String send, String title, String content,String ip);

    void insertCodeMessage(String send,String title ,String content, int i, String ipAddress,boolean success);
}
