package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.*;

import java.util.List;
import java.util.Map;

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

    PageBean<Guard> getGuardList(int curren, String guardName, String phone, Integer company, Integer state);

    Guard selectGuardByID(Integer id);


    Result reviewGuard(Guard guard);

    Result disableGuard(Guard guard);

    Result updateguard(Guard guard);

    Result rePasswordGuard(Guard guard);

    Result updateAdmin(Admin admin);

    Result deletedGuardByID(Integer id);

    Result batchAllowedGuards(Map<String, Integer> map);
}
