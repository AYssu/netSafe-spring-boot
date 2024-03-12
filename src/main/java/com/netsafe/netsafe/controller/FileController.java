package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Files;
import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.service.FileService;
import com.netsafe.netsafe.utils.FileUtils;
import com.netsafe.netsafe.utils.LogUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.savePath}")
    public String UPLOAD_FOLDER;

    @Value("${file.local}")
    public String HTTP;

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestParam  MultipartFile file) throws IOException, NoSuchAlgorithmException {

        String md5 = getMD5(file.getInputStream());
        System.out.println("文件MD5值: " + md5);

        Files file1 = fileService.selectFileByMd5(md5);
        if (file1!=null)
        {
            LogUtil.LOG("文件已存在！");
            return  Result.success(file1.getUrl());
        }

        //获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        LogUtil.LOG("文件名称是：" + originalFilename);
        //获取文件的类型
        String type = FileUtils.getFileExtension(file);
        LogUtil.LOG("文件类型是：" + type);
        //获取文件大小
        long size = file.getSize();
        LogUtil.LOG("文件大小是：" + size);
        //获取文件
        File uploadParentFile = new File(UPLOAD_FOLDER);
        LogUtil.LOG("路径:"+UPLOAD_FOLDER);
        //判断文件目录是否存在
        if(!uploadParentFile.exists()) {
            //如果不存在就创建文件夹
            uploadParentFile.mkdirs();
        }
        //定义一个文件唯一标识码（UUID）
        String uuid = UUID.randomUUID().toString();


        String fileName = HTTP +  uuid  + type;
        LogUtil.LOG("文件完整路径:"+fileName);
        File uploadFile = new File(UPLOAD_FOLDER + uuid + type);
        //将临时文件转存到指定磁盘位置

        Files files = new Files();
        files.setId(0);
        files.setSize(size);
        files.setName(originalFilename);
        files.setType(type);
        files.setTime(LocalDateTime.now());
        files.setUrl(fileName);
        files.setMd5(md5);

        try {
            file.transferTo(uploadFile);
            Result result = fileService.insertFile(files);
            return  result;
        } catch (IOException e) {
            return Result.error("上传失败");
        }
    }

    private String getMD5(InputStream input) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (BufferedInputStream bis = new BufferedInputStream(input)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
