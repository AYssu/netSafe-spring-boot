package com.netsafe.netsafe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Send {
    private Integer id;
    private String sendTo;
    private String content;
    private String title;
    private Integer type;
    private String ip;
    private LocalDateTime time;
    private Integer success;
}
