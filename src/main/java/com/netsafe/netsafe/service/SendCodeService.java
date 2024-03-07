package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Result;

public interface SendCodeService {

    Result send(String phone);


    Result checkCode(String phone, String code);
}
