package com.netsafe.netsafe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    //处理总数
    private Integer total;
    //成功总数
    private Integer success;
    //失败总数
    private Integer error;
    //失败用户列表
    private  List<String> stringList;
}
