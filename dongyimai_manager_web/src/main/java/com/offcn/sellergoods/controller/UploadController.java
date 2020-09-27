package com.offcn.sellergoods.controller;


import com.offcn.common.utils.FastDFSClient;
import com.offcn.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        // 1.获取之前的文件名
        String originalFilename = file.getOriginalFilename();
        // 2.获取文件的扩展名  asdfaf.jpg
        int index = originalFilename.lastIndexOf(".");
        String extName= originalFilename.substring(index + 1);
        // 3.创建 工具类的对象
        try {
            FastDFSClient client = new FastDFSClient("classpath:fdfs_client.conf");
            String path = client.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL + path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }


    }
}
