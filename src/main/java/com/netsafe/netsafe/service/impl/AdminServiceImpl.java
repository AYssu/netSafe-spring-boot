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
import com.netsafe.netsafe.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
            stringStringValueOperations.set(s, s, 365, TimeUnit.DAYS);
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

    private boolean checkObjectNull(String object)
    {
        return object!=null&& !object.isEmpty() &&!object.equals("null")&&!object.equals("undefined");
    }

    @Override
    public PageBean<Guard> getGuardList(int curren, String guardName, String phone, Integer company, Integer state) {

        Page<Guard> guardPage = new Page<>(curren, 15);
        LambdaQueryWrapper<Guard> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(checkObjectNull(guardName),Guard::getGuardName,guardName);
        queryWrapper.like(checkObjectNull(phone),Guard::getPhone,phone);
        queryWrapper.eq(company!=null,Guard::getCid,company);
        queryWrapper.eq(state!=null,Guard::getState,state);
        Page<Guard> guardPage1 = guardMapper.selectPage(guardPage, queryWrapper);

        PageBean<Guard> pageBean = new PageBean<>();
        LogUtil.LOG("共:"+guardPage1.getTotal());
        pageBean.setTotal(guardPage1.getTotal());
        pageBean.setItems(guardPage1.getRecords());
        return pageBean;
    }

    @Override
    public Guard selectGuardByID(Integer id) {
        return guardMapper.selectById(id);
    }

    @Override
    public Result reviewGuard(Guard guard) {
        LogUtil.LOG("审核:"+guard.toString());
        if (guard.getState()!=2)
        {
            return Result.error("该保安状态不能审核");
        }
        LambdaUpdateWrapper<Guard> groupLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        groupLambdaQueryWrapper.eq(Guard::getId,guard.getId());
        groupLambdaQueryWrapper.set(Guard::getState,0);
        int i = guardMapper.update(groupLambdaQueryWrapper);

        if (i>0)
        {
            return Result.success();
        }
        return Result.error("审核失败");
    }

    @Override
    public Result disableGuard(Guard guard) {
        LogUtil.LOG("禁用:"+guard.toString());
        LambdaUpdateWrapper<Guard> guardLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        guardLambdaUpdateWrapper.eq(Guard::getId,guard.getId());
        if (guard.getState()==1)
        {
            guardLambdaUpdateWrapper.set(Guard::getState,2);
        }else{
            guardLambdaUpdateWrapper.set(Guard::getState,1);
        }
        int i = guardMapper.update(guardLambdaUpdateWrapper);
        if (i>0)
        {
            return Result.success();
        }
        return Result.error("禁用/解禁失败!");
    }

    @Override
    public Result updateguard(Guard guard) {
        int i = guardMapper.updateById(guard);
        if (i>0)
            return Result.success();
        return Result.error("更新用户信息失败");
    }

    @Override
    public Result rePasswordGuard(Guard guard) {
        String salt = StringUtil.randomStr(6);

        String password = StringUtil.randomStr(10);
        String md5 = MD5Util.getMD5(password,salt,10);
        guard.setSalt(salt);
        guard.setPassword(md5);
        guard.setUpdateTime(LocalDateTime.now());
        int i = guardMapper.updateById(guard);
        if (i>0)
        {
            return Result.success(password);
        }
        return Result.error("重置失败");
    }

    @Override
    public Result updateAdmin(Admin admin) {
        int i = adminMapper.updateById(admin);
        if (i>0)
        {
            return Result.success();
        }
        return Result.error("更新用户数据失败");
    }


    @Override
    public Result deletedGuardByID(Integer id) {
        int i = guardMapper.deleteById(id);
        if (i>0)
            return Result.success();
        return Result.error("删除用户失败");
    }

    @Override
    public Result batchAllowedGuards(Map<String, Integer> map) {

        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        Integer total = map.size();
        Integer success = 0;
        List<String> errorGuard = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries)
        {
            LambdaUpdateWrapper<Guard> guardLambdaQueryWrapper = new LambdaUpdateWrapper<>();
            guardLambdaQueryWrapper.eq(Guard::getId,entry.getValue());
            guardLambdaQueryWrapper.eq(Guard::getState,2);
            guardLambdaQueryWrapper.set(Guard::getState,0);
            int i = guardMapper.update(guardLambdaQueryWrapper);
            if (i>0)
                success++;
            else
                errorGuard.add(entry.getKey());
        }

        Batch batch = new Batch(total,success,total-success,errorGuard);

        if (success==0)
        {
            return Result.error("操作失败!");
        }

        return Result.success(batch);
    }

}
