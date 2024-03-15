package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.*;

import java.util.List;

public interface AdminService {
    Result login(String adminname, String password);

    Admin selectAdminByID(Integer id);

    List<Group> selectGroups();

    Group selectGroupByID(Integer id);

    Result updateGroup(Integer id, String groupName);

    Group selectGroupByName(String groupName);

    Result insertGroup(String groupName);

    Result insertGuard(Guard guard);

    Guard selectGuardByPhone(String phone);
    PageBean<Guard> getGuardList(int curren);
}
