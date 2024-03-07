package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.netsafe.netsafe.mapper.MailMapper;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.RedisService;
import com.netsafe.netsafe.service.SendCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SendCodeServiceImpl implements SendCodeService {

    @Autowired
    private MailMapper mailMapper;

    @Autowired
    private RedisService redisService;

    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    //过期时间60秒
    @Value("${redis.key.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Override
    public Result send(String phone,String ip) {
        String redisauthcode= redisService.get(REDIS_KEY_PREFIX_AUTH_CODE+phone);
        if(!StringUtils.isEmpty(redisauthcode)){
            //如果未取到则过期
            insertCodeMessage(phone,"手机验证码","请勿重复发送",0,ip,false);

            return Result.error("请勿重复发送!");
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_CODE+phone,sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_CODE+phone,AUTH_CODE_EXPIRE_SECONDS);
        insertCodeMessage(phone,"手机验证码","发送成功:"+sb,0,ip,true);
        return Result.success(sb.toString());
    }


    @Override
    public Result checkCode(String phone, String code) {
        String redisauthcode= redisService.get(REDIS_KEY_PREFIX_AUTH_CODE+phone);
        if(StringUtils.isEmpty(redisauthcode)){
            //如果未取到则过期
            return Result.error("验证码已失效");
        }
        if(!"".equals(redisauthcode)&&!code.equals(redisauthcode)){
            return Result.error("验证码错误");
        }
        //验证验证码成功就删除验证码了
        redisService.remove(REDIS_KEY_PREFIX_AUTH_CODE+phone);
        return  Result.success("用户注册成功");
    }

    @Override
    public void insertCodeMessage(String send,String title ,String content, int i, String ipAddress,boolean success) {
        mailMapper.inserCodeMessage(send,title,content,i,ipAddress,success);
    }


}
