package com.netsafe.netsafe.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailMapper {

    @Insert("insert into send(sendTo, content, title, type, ip, time, success) " +
            "VALUES (#{send},#{content},#{title},#{i},#{ipAddress},now(),#{success})")
    void inserCodeMessage(String send, String title, String content, int i, String ipAddress,boolean success);
}
