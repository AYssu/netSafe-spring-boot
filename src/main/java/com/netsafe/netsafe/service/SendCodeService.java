package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;

public interface SendCodeService {

    Result send(String phone,String ip);


    Result checkCode(String phone, String code);

    void insertCodeMessage(String send,String title ,String content, int i, String ipAddress,boolean success);

}
