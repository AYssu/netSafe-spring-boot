package com.netsafe.netsafe;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@SpringBootTest
@Component
class NetSafeApplicationTests {

   @Autowired
   private MailService mailService;
    @Test
    void contextLoads() throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setDefaultEncoding("utf-8");

        javaMailSender.setHost("smtp.163.com");              // 设置邮箱服务器
        javaMailSender.setPort(25);                        // 设置端口
        javaMailSender.setUsername("18281778980@163.com");    // 设置用户名
        javaMailSender.setPassword("LEBOWXTAXSLKYBEQ");      // 设置密码（记得替换为你实际的密码、授权码）
        javaMailSender.setProtocol("smtp");                // 设置协议

        Properties properties = new Properties();           // 配置项
        properties.put("mail.smtp.connectiontimeout", 5000);
        properties.put("mail.smtp.timeout", 3000);
        properties.put("mail.smtp.writetimeout", "5000");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true);
//
//
        javaMailSender.setJavaMailProperties(properties); // 设置配置项

        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        // 创建 MimeMessageHelper
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        // 发件人邮箱和名称
        helper.setFrom("18281778980@163.com");
        // 收件人邮箱
        helper.setTo("2997036064@qq.com");
        // 邮件标题
        helper.setSubject("Hello");
        // 邮件正文，第二个参数表示是否是HTML正文
        helper.setText("Hello <strong> World</strong>！", true);

        // 发送
        javaMailSender.send(message);
    }

}
