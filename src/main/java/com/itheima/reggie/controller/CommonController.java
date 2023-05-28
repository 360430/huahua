package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/9:27
 * @Description:
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String>upload(MultipartFile file) throws IOException {

        //获取源文件名和后缀名
        String originalFilename = file.getOriginalFilename();

        //将后缀名截取下来
        assert originalFilename != null;
        String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String uuid = String.valueOf(UUID.randomUUID());

        //新的文件名+后缀名
        String fileName=uuid+prefix;
        file.transferTo(new File(basePath+fileName));

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/download")
    public R<String>downLoad(String name, HttpServletResponse response) throws IOException {

        //将存储的文件流进行对拷
        FileInputStream fis = new FileInputStream(basePath + name);
        ServletOutputStream os = response.getOutputStream();

        IOUtils.copy(fis,os);
        return R.success("文件下载成功");
    }

}
