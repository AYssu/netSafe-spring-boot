package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.pojo.User;

public interface UserService {
    User selectUserByName(String username);

    void register(String username, String password, String organization, String phone);


    boolean login(User user, String password);

    User selectUserByPhone(String phone);

    User selectUserByID(int id);
}
