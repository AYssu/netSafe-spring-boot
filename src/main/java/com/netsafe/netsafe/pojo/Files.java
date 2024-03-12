package com.netsafe.netsafe.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.lettuce.core.dynamic.annotation.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@TableName(value = "file")
@Data
@AllArgsConstructor
public class Files {
    @Id
    private Integer id;

    private String name;
    private String type;
    private long size;
    LocalDateTime time;
    private String url;
    private String md5;
    private String is_delete;

    public Files() {

    }
}
