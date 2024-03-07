package com.netsafe.netsafe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.netsafe.netsafe.mapper"})
@SpringBootApplication
public class NetSafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetSafeApplication.class, args);
    }

}
