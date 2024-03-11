package com.netsafe.netsafe.service.impl;

import com.netsafe.netsafe.service.AsyncService;
import com.netsafe.netsafe.utils.LogUtil;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AsyncServiceImpl implements AsyncService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Async("taskExecutor")
    @Override
    public void sendEmailAsync(Instant now, MimeMessage mimeMessage) {
        LogUtil.LOG("异步请求开始");
        javaMailSender.send(mimeMessage);
        Instant finalTime = Instant.now();
        Duration durationBetween = Duration.between(now, finalTime);
        LogUtil.LOG("代码执行后部分时间:" + durationBetween);
    }
}
