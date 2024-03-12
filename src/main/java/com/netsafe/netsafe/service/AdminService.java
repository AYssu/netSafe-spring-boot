package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;

public interface AdminService {
    Result login(String adminname, String password);
}
