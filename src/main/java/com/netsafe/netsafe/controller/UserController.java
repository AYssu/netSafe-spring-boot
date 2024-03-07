package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.pojo.User;
import com.netsafe.netsafe.service.MailService;
import com.netsafe.netsafe.service.SendCodeService;
import com.netsafe.netsafe.service.UserService;
import com.netsafe.netsafe.utils.JwtUtil;
import com.netsafe.netsafe.utils.LogUtil;
import com.netsafe.netsafe.utils.ThreadLocalUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
import java.util.regex.Pattern;

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
    public Result register( String username, String password,String organization,String phone,String code)
    {
        //使用@Pattern(regexp = "^\\S{4,16}$") 也能拦截 但是返回的数据无法被前端直接了解 对用户不友好
        if (username.length()<2)
        {
            return Result.error("用户名过短！");
        }

        if (password.length()<6)
        {
            return Result.error("密码过短！");
        }

        if (organization.length()<2)
        {
            return Result.error("组织名称过短！");
        }

        if (phone.length()<11)
        {
            return Result.error("手机号过短！");
        }

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
    public Result login(String username,String password)
    {
        //使用@Pattern(regexp = "^\\S{4,16}$") 也能拦截 但是返回的数据无法被前端直接了解 对用户不友好
        if (username.length()<2)
        {
            return Result.error("用户名过短！");
        }

        if (password.length()<6)
        {
            return Result.error("密码过短！");
        }

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
    public Result send(String phone)
    {
        if (phone==null)
        {
            return Result.error("手机号为必传参数");
        }
        if (phone.length()<11)
        {
            return Result.error("手机号格式不正确！");
        }
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);

        // 检查手机号格式
        if (!pattern.matcher(phone).matches()) {
            return Result.error("手机号格式不正确！");
        }
        User user = userService.selectUserByPhone(phone);
        if (user!=null)
        {
            return Result.error("该手机号已经注册！");
        }

        return sendCodeService.send(phone);
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
    public Result sendMail(@Email(message = "邮箱格式错误") String send){
        Result result = new Result();

        try {
            result =  mailService.sendMail(send);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.error("验证码发送失败！");

        }
        if (result.getCode()==0)
        {
            return result;
        }
        return Result.success();
    }

    @GetMapping("/sendMail")
    public Result sendMail(@Email(message = "邮箱格式有误！") String send, @NotNull(message = "不能为空") String title, @NotNull(message = "不能为空") String content){
        Result result = mailService.sendMail(send,title,content);
        if (result.getCode()==0)
            return result;
        return Result.success();
    }
}
