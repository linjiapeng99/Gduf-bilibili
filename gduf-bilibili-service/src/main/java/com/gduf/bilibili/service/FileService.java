package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.FileDao;
import com.gduf.bilibili.domain.File;
import com.gduf.bilibili.service.util.FastDFSUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class FileService {
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private FileDao fileDao;


    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        File dbFileMd5 = fileDao.getFileByMd5(fileMd5);
        //如果数据库中存在文件标识，则说明已经上传过该文件，可以直接返回地址
        if (dbFileMd5 != null) {
            return dbFileMd5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        //如果该文件历史没有上传过，而且上传成功，那么就要在 数据库中标识
        if (!StringUtils.isNullOrEmpty(url)) {
            File file = new File();
            file.setMd5(fileMd5);
            file.setUrl(url);
            file.setType(fastDFSUtil.getFileType(slice));
            file.setCreateTime(new Date());
            fileDao.addFile(file);
        }
        return url;
    }

    /**
     * 获取文件的md5唯一标识
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String getFileMd5(MultipartFile file) throws Exception {
        InputStream fis = file.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int byteRead;
        while ((byteRead = fis.read(buffer)) > 0) {
            baos.write(buffer, 0, byteRead);
        }
        fis.close();
        return DigestUtils.md5Hex(baos.toByteArray());
    }
}
