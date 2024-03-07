package com.netsafe.netsafe.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class User {
    private Integer id;//主键ID
    private String username;//用户名
    private String organization;//组织
    private String password;//密码
    private String salt;//盐值
    private String nickname;//昵称
    private String email;//邮箱
    private String phone;//手机号
    private String userPic;//用户头像地址
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    public User(){

    }
}
