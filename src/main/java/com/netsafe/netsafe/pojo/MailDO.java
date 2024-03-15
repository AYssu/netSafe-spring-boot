package com.netsafe.netsafe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDO {
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 接收人邮箱
     */
    private String[] email;
    /**
     * 附加，value 文件的绝对地址/动态模板数据
     */
    private Map<String, Object> attachment;

}
