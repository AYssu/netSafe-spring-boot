package com.netsafe.netsafe.exception;

import com.mysql.cj.util.StringUtils;
import com.netsafe.netsafe.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e)
    {
        e.printStackTrace();
        return Result.error(StringUtils.isNullOrEmpty(e.getMessage()) ? "操作失败" : e.getMessage());
    }
}
