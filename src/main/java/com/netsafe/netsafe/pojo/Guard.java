package com.netsafe.netsafe.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "guard")
@Validated
public class Guard {
    @Id
    private Integer id;
    @NotNull(message = "名字为必传参数")
    @Pattern(regexp = "^\\S{2,8}$",message = "名字为2-8个字符")
    private String guardName;

    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号不合法")
    private String phone;
    private Integer cid;
    private String master;
    private String password;
    private String salt;
    private Integer state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
