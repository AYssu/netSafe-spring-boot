package com.netsafe.netsafe.service;

import com.netsafe.netsafe.pojo.Files;
import com.netsafe.netsafe.pojo.Result;

public interface FileService {

    Files selectFileByMd5(String md5);

    Result insertFile(Files files);
}
