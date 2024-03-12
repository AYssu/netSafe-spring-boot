package com.netsafe.netsafe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netsafe.netsafe.pojo.Files;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<Files> {
}
