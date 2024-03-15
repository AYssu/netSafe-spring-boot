package com.netsafe.netsafe.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netsafe.netsafe.pojo.*;
import com.netsafe.netsafe.service.AdminService;
import com.netsafe.netsafe.utils.LogUtil;
import com.netsafe.netsafe.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Validated
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

    @GetMapping("/userInfo")
    public Result getAdminInformation()
    {
        Map<String ,Object> map = ThreadLocalUtil.get();
        int id =(Integer) map.get("id");
        System.out.println("Thread uid："+ id);

        Admin admin = adminService.selectAdminByID(id);
        if (admin==null)
        {
            return Result.error("用户不存在");
        }
        return Result.success(admin);
    }

    @PostMapping("/addGroup")
    public Result addGroup(String groupName)
    {
        Group group = adminService.selectGroupByName(groupName);
        if (group!=null)
        {
            return Result.error("已经有组织名为:"+groupName);
        }

        return adminService.insertGroup(groupName);
    }
    @GetMapping("/getGroup")
    public Result getGroup()
    {
        List<Group> groups = adminService.selectGroups();
        return Result.success(groups);
    }

    @PostMapping("/updateGroup")
    public Result updateGroup(@NotNull(message = "id为必传参数") Integer id,@NotNull(message = "新名字为必传参数") @Pattern(regexp = "^\\S{4,16}$",message = "必须满足名字长度大于4") String groupName)
    {
        Group group = adminService.selectGroupByID(id);
        if (group==null)
        {
            return Result.error("组织为空");
        }

        Group group1 = adminService.selectGroupByName(groupName);
        if (group1!=null)
        {
            return Result.error("已经有组织名为:"+groupName);
        }

        return adminService.updateGroup(id,groupName);
    }

    @PostMapping("/addGuard")
    public Result addGuard(@RequestBody @Validated Guard guard)
    {
        guard.setId(0);
        guard.setCreateTime(LocalDateTime.now());
        guard.setUpdateTime(LocalDateTime.now());
        Guard guard1 = adminService.selectGuardByPhone(guard.getPhone());
        if (guard1!=null)
        {
            return Result.error("该手机号已经被用户:"+guard1.getGuardName()+"绑定！");
        }

        Group group = adminService.selectGroupByID(guard.getCid());
        if (group==null)
        {
            return Result.error("绑定的组织不存在");
        }

        return adminService.insertGuard(guard);
    }

    @PostMapping("/getGuard")
    public Result getGuard(String curren)
    {
        LogUtil.LOG("查询页数："+curren);
        PageBean<Guard> pageBean =  adminService.getGuardList(Integer.parseInt(curren));
        return Result.success(pageBean);
    }
}
