package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.netsafe.netsafe.mapper.MailMapper;
import com.netsafe.netsafe.pojo.MailDO;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.AsyncService;
import com.netsafe.netsafe.service.MailService;
import com.netsafe.netsafe.service.RedisService;
import com.netsafe.netsafe.utils.LogUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MailServiceImpl implements MailService {


    @Autowired
    private MailMapper mailMapper;
    @Autowired
    private JavaMailSender javaMailSender;
    //template模板引擎
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private RedisService redisService;

    @Value("${redis.key.prefix.authMail}")
    private String REDIS_KEY_PREFIX_AUTH_Mail;
    //过期时间60秒
    @Value("${redis.key.expire.authMail}")
    private Long AUTH_Mail_EXPIRE_SECONDS;



    @Override
    public Result sendMail(String send,String ip) throws MessagingException {
        Instant start = Instant.now();
        MailDO mailDO = new MailDO();
        String redisauthmail = redisService.get(REDIS_KEY_PREFIX_AUTH_Mail + send);
        if (!StringUtils.isEmpty(redisauthmail)) {
            //如果未取到则过期
            insertCodeMessage(send,"邮箱验证码","请勿重复发送",1,ip,false);
            return Result.error("请勿重复发送!");
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        String[] user = new String[1];
        user[0] = send;
        Map<String, Object> map = new HashMap<>();
        map.put("code", sb.toString());
        mailDO.setEmail(user);
        mailDO.setTitle("发送验证码");
        mailDO.setContent(sb.toString());
        mailDO.setAttachment(map);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
            // 获取 MimeMessage
            Session session = mimeMessage.getSession();
            // 设置 日志打印控制器
            session.setDebug(false);
            //  解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
            session.getProperties().setProperty("mail.smtp.localhost", "localhost");

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
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
            String emailContent = templateEngine.process("indexPatternMail.html", context); //指定模板路径
            messageHelper.setText(emailContent, true);
            Instant now = Instant.now();
            Duration duration = Duration.between(start,now);
            LogUtil.LOG("代码执行前部分时间:"+duration);
            //发送邮件
            //使用多线程 优化用户体验
            //这边最快能一秒做出相应 但是修修改改 也没有改什么就会变成10s 之前是因为ITAM和SSL没有打开

//            Thread thread = new Thread(() ->
//            {
//                javaMailSender.send(mimeMessage);
//                Instant finalTime = Instant.now();
//                Duration durationBetween = Duration.between(now,finalTime);
//                LogUtil.LOG("代码执行后部分时间:"+durationBetween);
//                insertCodeMessage(send,"邮箱验证码","发送成功:"+sb,1,ip,true);
//
//            });
//            thread.start();

            asyncService.sendEmailAsync(now,mimeMessage);

            //貌似使用这个会更好 2024/3/10 已修复 问题是因为Async不支持同一个类里面的方法

            LogUtil.LOG("发送请求完成");

        } catch (MessagingException e) {
            e.printStackTrace();
            insertCodeMessage(send,"邮箱验证码","发送失败:"+sb,1,ip,false);

            return Result.error("邮件发送失败");
        }
        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_Mail + send, sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_Mail + send, AUTH_Mail_EXPIRE_SECONDS);
        return Result.success();
    }

    @Override
    public Result checkMail(String send) {
        String redisauthcode = redisService.get(REDIS_KEY_PREFIX_AUTH_Mail + send);
        if (StringUtils.isEmpty(redisauthcode)) {
            //如果未取到则过期
            return Result.error("验证码已失效");
        }
        if (!"".equals(redisauthcode) && !send.equals(redisauthcode)) {
            return Result.error("验证码错误");
        }
        //验证验证码成功就删除验证码了
        redisService.remove(REDIS_KEY_PREFIX_AUTH_Mail + send);
        return Result.success("用户注册成功");
    }

    @Override
    public Result sendMail(String send, String title, String content,String ip) {
        LogUtil.LOG("文本对象："+content);
        Instant start = Instant.now();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
            // 获取 MimeMessage
            Session session = mimeMessage.getSession();
            // 设置 日志打印控制器
            session.setDebug(false);
            //  解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
            session.getProperties().setProperty("mail.smtp.localhost", "localhost");

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            // 发送人的邮箱
            messageHelper.setFrom(from);
            //发给谁  对方邮箱
            messageHelper.setTo(send);
            //标题
            messageHelper.setSubject(title);
            //使用模板thymeleaf
            //定义模板数据
            //获取thymeleaf的html模板
            messageHelper.setText(content,false);
            //这边最快能一秒做出相应 但是修修改改 也没有改什么就会变成10s 之前是因为ITAM和SSL没有打开
            Instant now = Instant.now();
            Duration duration = Duration.between(start,now);
            LogUtil.LOG("代码执行前部分时间:"+duration);
//            Thread thread = new Thread(() ->
//            {
//                javaMailSender.send(mimeMessage);
//                Instant finalTime = Instant.now();
//                Duration durationBetween = Duration.between(now,finalTime);
//                LogUtil.LOG("代码执行后部分时间:"+durationBetween);
//                insertCodeMessage(send,title,content,2,ip,true);
//
//            });
//            thread.start();

            asyncService.sendEmailAsync(now,mimeMessage);




            LogUtil.LOG("发送请求完成");

        } catch (MessagingException e) {
            e.printStackTrace();
            insertCodeMessage(send,title,"发送失败:"+content,2,ip,false);

            return Result.error("邮件发送失败");
        }
        return Result.success();
    }

    @Override
    public void insertCodeMessage(String send,String title ,String content, int i, String ipAddress,boolean success) {
        mailMapper.inserCodeMessage(send,title,content,i,ipAddress,success);
    }

}
