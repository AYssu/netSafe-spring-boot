package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.netsafe.netsafe.pojo.MailDO;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.MailService;
import com.netsafe.netsafe.service.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JavaMailSender javaMailSender;
    //template模板引擎
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String from;


    @Autowired
    private RedisService redisService;

    @Value("${redis.key.prefix.authMail}")
    private String REDIS_KEY_PREFIX_AUTH_Mail;
    //过期时间60秒
    @Value("${redis.key.expire.authMail}")
    private Long AUTH_Mail_EXPIRE_SECONDS;

    @Override
    public Result sendMail(String send) {
        MailDO mailDO = new MailDO();
        String redisauthmail = redisService.get(REDIS_KEY_PREFIX_AUTH_Mail + send);
        if (!StringUtils.isEmpty(redisauthmail)) {
            //如果未取到则过期
            return Result.error("请勿重复发送!");
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        String[] user = new  String[1];
        user[0] = send;
        Map<String, Object> map = new HashMap<>();
        map.put("code",sb.toString());
        mailDO.setEmail(user);
        mailDO.setTitle("发送验证码");
        mailDO.setContent(sb.toString());
        mailDO.setAttachment(map);
        SimpleMailMessage message = new SimpleMailMessage();

        // 发送人的邮箱
        message.setFrom(from);
        //标题
        message.setSubject(mailDO.getTitle());
        //发给谁  对方邮箱
        message.setTo(mailDO.getEmail());
        //内容
        message.setText("你的验证码:"+mailDO.getContent());
        try {
            //发送
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            return Result.error("邮件发送失败");
        }
        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_Mail + send, sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_Mail + send, AUTH_Mail_EXPIRE_SECONDS);
        return Result.success();
    }

    @Override
    public Result sendMailHtml(String send) {
        MailDO mailDO = new MailDO();
        String redisauthmail = redisService.get(REDIS_KEY_PREFIX_AUTH_Mail + send);
        if (!StringUtils.isEmpty(redisauthmail)) {
            //如果未取到则过期
            return Result.error("请勿重复发送!");
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        String[] user = new  String[1];
        user[0] = send;
        Map<String, Object> map = new HashMap<>();
        map.put("code",sb.toString());
        mailDO.setEmail(user);
        mailDO.setTitle("发送验证码");
        mailDO.setContent(sb.toString());
        mailDO.setAttachment(map);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
//            // 获取 MimeMessage
//            Session session = mimeMessage.getSession();
//            // 设置 日志打印控制器
//            session.setDebug(true);
//            //  解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
//            session.getProperties().setProperty("mail.smtp.localhost", "127.0.0.1");

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            // 发送人的邮箱
            messageHelper.setFrom(from);
            //发给谁  对方邮箱
            messageHelper.setTo(send);
            //标题
            messageHelper.setSubject("短信验证码");
            //使用模板thymeleaf
            //Context是导这个包import org.thymeleaf.context.Context;
            Context context = new Context();
            //定义模板数据
            context.setVariables(mailDO.getAttachment());
            //获取thymeleaf的html模板
            String emailContent = templateEngine.process("indexPatternMail.html",context); //指定模板路径
            messageHelper.setText(emailContent,true);
            //发送邮件
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.error("邮件发送失败");
        }

        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_Mail + send, sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_Mail + send, AUTH_Mail_EXPIRE_SECONDS);
        return Result.success();
    }

    @Override
    public Result checkMail(String send) {
        return Result.success();
    }

    @Override
    public void test() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        // 发送人的邮箱
        message.setFrom(from);
        //标题
        message.setSubject("没有");
        //发给谁  对方邮箱
        message.setTo("2997036064@qq.com");
        //内容
        message.setText("你的验证码:10086");
        try {
            //发送
            javaMailSender.send(message.getMimeMessage());
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
