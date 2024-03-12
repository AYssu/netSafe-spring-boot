package com.netsafe.netsafe;

import com.netsafe.netsafe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NetSafeApplication {


    public static void main(String[] args) {
        SpringApplication.run(NetSafeApplication.class, args);
    }

}
