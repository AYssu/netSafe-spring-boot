package com.netsafe.netsafe.pojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;//更新时间

}
