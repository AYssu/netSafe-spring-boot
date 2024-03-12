package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class MainWelcome {
    @GetMapping
    public Result<String> tables()
    {
        return Result.success("");
    }
}
