package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.AdminService;
import com.netsafe.netsafe.utils.LogUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @PostMapping("/login")
    public Result login(@NotNull(message = "用户为必传字段") @Pattern(regexp = "^\\S{4,16}$" ,message = "账号长度4-16")String adminname, @NotNull(message = "密码为必传字段") @Pattern(regexp = "^\\S{4,16}$" ,message = "密码长度4-16") String password,  @NotNull(message = "验证码为必传字段") @Pattern(regexp = "^\\S{4}$" ,message = "验证码长度4") String code, HttpSession session)
    {
        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        LogUtil.LOG("你的验证码: "+ code + "你的attr:"+random);
        if (random == null || "".equals(random) || !random.equalsIgnoreCase(code)) {
            return Result.error("验证码错误");
        }


        return adminService.login(adminname,password);
    }
}
