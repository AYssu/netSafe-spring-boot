package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netsafe.netsafe.mapper.AdminMapper;
import com.netsafe.netsafe.mapper.GroupMapper;
import com.netsafe.netsafe.mapper.GuardMapper;
import com.netsafe.netsafe.pojo.*;
import com.netsafe.netsafe.service.AdminService;
import com.netsafe.netsafe.utils.JwtUtil;
import com.netsafe.netsafe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GuardMapper guardMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(String adminname, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adminname", adminname);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin == null) {
            LogUtil.LOG("登录管理员:" + adminname + ",password:" + password);
            return Result.error("管理员不存在");
        }

        if (admin.getPassword().equals(password)) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", admin.getId());
            map.put("adminname", admin.getAdminname());
            String s = JwtUtil.genToken(map);
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            stringStringValueOperations.set(s, s, 1, TimeUnit.HOURS);
            return Result.success(s);
        }

        return Result.error("密码错误");
    }

    @Override
    public Admin selectAdminByID(Integer id) {
        return adminMapper.selectById(id);
    }

    @Override
    public List<Group> selectGroups() {
        return groupMapper.selectList(null);
    }

    @Override
    public Group selectGroupByID(Integer id) {
        return groupMapper.selectById(id);
    }

    @Override
    public Result updateGroup(Integer id, String groupName) {

        LambdaUpdateWrapper<Group> groupLambdaUpdateChainWrapper = new LambdaUpdateWrapper<>();
        groupLambdaUpdateChainWrapper.eq(Group::getId, id).set(Group::getGroupName, groupName).set(Group::getChangeTime, LocalDateTime.now());

        int i = groupMapper.update(groupLambdaUpdateChainWrapper);
        if (i > 0) {
            return Result.success("修改成功");
        }
        return Result.error("更新失败");
    }

    @Override
    public Group selectGroupByName(String groupName) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Group::getGroupName, groupName);
        return groupMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Result insertGroup(String groupName) {
        Group group = new Group(0, groupName, LocalDateTime.now(), LocalDateTime.now());
        if (groupMapper.insert(group) > 0) {
            return Result.success("创建:" + groupName + "成功");
        }
        return Result.error("添加组织失败");
    }

    @Override
    public Result insertGuard(Guard guard) {
        if (guardMapper.insert(guard) > 0)
            return Result.success("保安添加成功");
        return Result.error("添加失败");
    }

    @Override
    public Guard selectGuardByPhone(String phone) {
        LambdaQueryWrapper<Guard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Guard::getPhone, phone);
        return guardMapper.selectOne(queryWrapper);
    }

    @Override
    public PageBean<Guard> getGuardList(int curren) {
        Page<Guard> guardPage = new Page<>(curren, 15);
        LambdaQueryWrapper<Guard> queryWrapper = new LambdaQueryWrapper<>();
        Page<Guard> guardPage1 = guardMapper.selectPage(guardPage, queryWrapper);
        LogUtil.LOG("共:" + guardPage1.getTotal());
        LogUtil.LOG("共:" + guardPage1.getPages());
        LogUtil.LOG("共:" + guardPage1.getSize());
        LogUtil.LOG("共:" + guardPage1.getRecords().toString());

        PageBean<Guard> pageBean = new PageBean<>();
        pageBean.setTotal(guardPage1.getTotal());
        pageBean.setItems(guardPage1.getRecords());
        return pageBean;
    }

}
