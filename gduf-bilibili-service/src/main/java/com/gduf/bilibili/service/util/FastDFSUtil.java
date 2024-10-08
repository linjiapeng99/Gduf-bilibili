package com.gduf.bilibili.service.util;

import com.gduf.bilibili.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashSet;
import java.util.Set;

public class FastDFSUtil {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    //获取文件类型
    public String getFileType(MultipartFile file){
        if(file==null){
            throw new ConditionException("非法文件");
        }
        String filename = file.getOriginalFilename();
        int index=filename.lastIndexOf(".");
        return filename.substring(index);
    }

    //上传
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet=new HashSet<>();
        //MetaData metaData=new MetaData();
        String fileType=this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    //删除
    public void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }
}
