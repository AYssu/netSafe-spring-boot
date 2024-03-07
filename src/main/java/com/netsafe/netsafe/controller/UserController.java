package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.pojo.User;
import com.netsafe.netsafe.service.MailService;
import com.netsafe.netsafe.service.SendCodeService;
import com.netsafe.netsafe.service.UserService;
import com.netsafe.netsafe.utils.IpUtil;
import com.netsafe.netsafe.utils.JwtUtil;
import com.netsafe.netsafe.utils.LogUtil;
import com.netsafe.netsafe.utils.ThreadLocalUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MailService mailService;

    /**
     * @param username 用户名
     * @param password 用户密码
     * @return code 1成功/0失败 成功返回data 失败返回message
     */
    @PostMapping("/register")
    public Result register( @NotNull(message = "密码为必传字段") @Pattern(regexp = "^\\S{4,16}$" ,message = "用户名长度4-16") String username, @NotNull(message = "密码为必传字段") @Pattern(regexp = "^\\S{4,16}$",message ="密码长度4-16" ) String password,@NotNull(message = "组织为必传字段")   String organization,@NotNull(message = "手机号为必传字段")  @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误") String phone,@NotNull(message = "验证码为必传字段") String code)
    {
        //使用@Pattern(regexp = "^\\S{4,16}$") 也能拦截 但是返回的数据无法被前端直接了解 对用户不友好

        LogUtil.LOG("注册用户:"+username);
        User user = userService.selectUserByName(username);

        if (user!=null)
        {
            return Result.error("用户已被注册");
        }
        Result i = sendCodeService.checkCode(phone,code);
        if (i.getCode()!=1)
        {
            return i;
        }

        userService.register(username,password,organization,phone);
        user = userService.selectUserByName(username);
        if (user==null)
        {
            return Result.error("用户注册失败!");
        }
        return Result.success("用户注册成功！");
    }

    @PostMapping("/login")
    public Result login(@NotNull(message = "密码为必传字段") @Pattern(regexp = "^\\S{4,16}$" ,message = "用户名长度4-16") String username, @NotNull(message = "密码为必传字段") @Pattern(regexp = "^\\S{4,16}$",message ="密码长度4-16" ) String password)
    {
        //使用 也能拦截 但是返回的数据无法被前端直接了解 对用户不友好

        LogUtil.LOG("登录用户:"+username);
        User user = userService.selectUserByName(username);
        if (user==null)
        {
            return Result.error("用户不存在！");
        }
        boolean i = userService.login(user,password);
        if (i)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("id",user.getId());
            map.put("username",user.getUsername());
            String s = JwtUtil.genToken(map);
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            stringStringValueOperations.set(s,s,1, TimeUnit.HOURS);
            return Result.success(s);
        }
        return Result.error("密码错误！");
    }

    @GetMapping("/send")
    public Result send(HttpServletRequest request ,@NotNull(message = "手机号为必传参数") @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误") String phone)
    {
        User user = userService.selectUserByPhone(phone);
        String ipAddress = IpUtil.getIpAddr(request);
        if (user!=null)
        {
//            sendCodeService.insertSend(phone,"尝试发送验证码","验证码",0,)
            return Result.error("该手机号已经注册！");
        }

        return sendCodeService.send(phone,ipAddress);
    }

    @GetMapping("/userInfo")
    public Result getUserInfo()
    {
        Map<String,Object> map = ThreadLocalUtil.get();
        if (map==null){
            return Result.error("用户校验失败！");
        }
        User user = userService.selectUserByID((int)map.get("id"));
        if (user!=null)
        {
            return Result.success(user);
        }
        return Result.error("获取用户失败！");
    }

    @PostMapping ("/sendMail")
    public Result sendMail(HttpServletRequest request , @Email(message = "邮箱格式错误") String send){
        Result result = new Result();
        String ipAddress = IpUtil.getIpAddr(request);

        try {
            result =  mailService.sendMail(send,ipAddress);
        } catch (MessagingException e) {
            e.printStackTrace();
            mailService.insertCodeMessage(send,"邮箱验证码","验证码发送失败",1,ipAddress,false);
            return Result.error("验证码发送失败！");
        }
        if (result.getCode()==0)
        {
            return result;
        }
        return Result.success();
    }

    @GetMapping("/sendMail")
    public Result sendMail(HttpServletRequest request ,@Email(message = "邮箱格式有误！") String send, @NotNull(message = "不能为空") String title, @NotNull(message = "不能为空") String content){
        String ipAddress = IpUtil.getIpAddr(request);
        Result result = mailService.sendMail(send,title,content,ipAddress);
        if (result.getCode()==0)
            return result;
        return Result.success();
    }
}
