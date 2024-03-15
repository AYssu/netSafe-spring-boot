package com.netsafe.netsafe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netsafe.netsafe.pojo.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {
}
