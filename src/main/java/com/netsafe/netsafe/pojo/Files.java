package com.netsafe.netsafe.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.lettuce.core.dynamic.annotation.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@TableName(value = "file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files {
    @Id
    private Integer id;

    private String name;
    private String type;
    private long size;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime time;
    private String url;
    private String md5;
    private String is_delete;

}
