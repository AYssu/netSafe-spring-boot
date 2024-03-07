package com.netsafe.netsafe.mapper;

import com.netsafe.netsafe.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UserMapper {
    //通过用户名查询 username唯一
    @Select("select * from user where username=#{username}")
    User selectUserByName(String username);

    //插入用户数据
    @Insert("insert into user(username,password,salt,organization,phone,create_time,update_time) values(#{username},#{password},#{salt},#{organization},#{phone},now(),now())")
    void register(User user);

    @Select("select * from user where phone=#{phone}")
    User selectUserByPhone(String phone);

    @Select("select * from user where id=#{id}")
    User selectUserByID(int id);
}
