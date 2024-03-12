package com.netsafe.netsafe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netsafe.netsafe.mapper.FileMapper;
import com.netsafe.netsafe.pojo.Files;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.FileService;
import com.netsafe.netsafe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Override
    public Files selectFileByMd5(String md5) {
        QueryWrapper<Files> filesQueryChainWrapper = new QueryWrapper<>();
        filesQueryChainWrapper.eq("md5",md5);
        return  fileMapper.selectOne(filesQueryChainWrapper);
    }

    @Override
    public Result insertFile(Files files) {
        int i = fileMapper.insert(files);
        if (i>0)
        {
            LogUtil.LOG("链接:"+files.getUrl());
            return Result.success(files.getUrl());
        }
        return Result.error("图片插入失败！");
    }
}
