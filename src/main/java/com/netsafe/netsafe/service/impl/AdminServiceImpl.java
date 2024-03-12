package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netsafe.netsafe.mapper.AdminMapper;
import com.netsafe.netsafe.pojo.Admin;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.AdminService;
import com.netsafe.netsafe.utils.JwtUtil;
import com.netsafe.netsafe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(String adminname, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adminname",adminname);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin==null)
        {
            LogUtil.LOG("登录管理员:"+adminname+",password:"+password);
            return Result.error("管理员不存在");
        }

        if (admin.getPassword().equals(password))
        {
            Map<String,Object> map = new HashMap<>();
            map.put("id",admin.getId());
            map.put("adminname",admin.getAdminname());
            String s = JwtUtil.genToken(map);
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            stringStringValueOperations.set(s,s,1, TimeUnit.HOURS);
            return Result.success(s);
        }

        return Result.error("密码错误");
    }
}
