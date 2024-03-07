package com.netsafe.netsafe.service.impl;

import com.netsafe.netsafe.mapper.UserMapper;
import com.netsafe.netsafe.pojo.User;
import com.netsafe.netsafe.service.UserService;
import com.netsafe.netsafe.utils.LogUtil;
import com.netsafe.netsafe.utils.MD5Util;
import com.netsafe.netsafe.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserByName(String username) {
        return userMapper.selectUserByName(username);
    }

    @Override
    public void register(String username, String password,String organization,String phone) {
        User user = new User();
        user.setUsername(username);
        //加盐
        String salt = StringUtil.randomStr(8);
        LogUtil.LOG("盐值:"+salt);
        user.setSalt(salt);
        user.setPhone(phone);
        user.setOrganization(organization);
        String password_salt = MD5Util.getMD5(password,salt,10);
        LogUtil.LOG("加盐密码:"+salt);
        user.setPassword(password_salt);
        userMapper.register(user);
    }

    @Override
    public boolean login(User user, String password) {
        String salt = user.getSalt();
        String password_salt = MD5Util.getMD5(password,salt,10);
        if (user.getPassword().equals(password_salt))
        {
            return true;
        }
        return false;
    }

    @Override
    public User selectUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public User selectUserByID(int id) {
        return userMapper.selectUserByID(id);
    }


}
