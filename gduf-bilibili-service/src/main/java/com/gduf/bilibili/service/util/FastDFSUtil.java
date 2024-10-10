package com.gduf.bilibili.service.util;

import com.gduf.bilibili.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
@Component
public class FastDFSUtil {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    //用于断点续传的依赖
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String,String>redisTemplate;

    private static final String DEFAULT_GROUP="group1";

    private static final String PATH_KEY="path-key:";

    private static final String UPLOAD_SIZE_KEY="upload-size-key:";

    private static final String UPLOAD_NO_KEY="upload-no-key:";

    //文件分片默认大小
    private static final int SLICE_SIZE=1024*1024*2;
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

    //文件上传，支持断点续传
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        //获取文件名主要是用来后续转为 md5 的，可以用来做秒传，避免重传
        String filename = file.getOriginalFilename();
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     * 该方法是用来支持断点续传的，原理是根据上传成功的第一个分片的路径，拼接接下来的二进制文件流
     * @param file
     * @param filePath  第一片文件路径
     * @param offset  偏移量
     * @throws Exception
     */
    public void modifyAppenderFile(MultipartFile file,String filePath,long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,file.getInputStream(),file.getSize(),offset);
    }

    public String uploadFileBySlices(MultipartFile file,String fileMd5,Integer sliceNo,Integer totalSliceNo) throws Exception {
        if(file==null||sliceNo==null||totalSliceNo==null){
            throw new ConditionException("参数异常");
        }
        //Redis中存储文件路径的键
        String pathKey=PATH_KEY+fileMd5;
        //Redis中存储文件总大小的键
        String uploadSizeKey=UPLOAD_SIZE_KEY+fileMd5;
        //Redis中存储文件分片是第几片的键
        String uploadNoKey=UPLOAD_NO_KEY+fileMd5;
        Long uploadSize=0L;
        //从Redis中拿文件大小
        String uploadSizeStr = redisTemplate.opsForValue().get(uploadSizeKey);
        if(!StringUtils.isNullOrEmpty(uploadSizeStr)){
            //如果Redis中文件大小存在，则需要对其进行初始化
            uploadSize=Long.valueOf(uploadSizeStr);
        }
        String fileType=this.getFileType(file);
        if(sliceNo==1){//前端上传的分片是第一片
            String path = this.uploadAppenderFile(file);
            if(StringUtils.isNullOrEmpty(path)){
                throw new ConditionException("上传失败");
            }
            //上传成功的话就要修改Redis中的路径以及文件大小等
            redisTemplate.opsForValue().set(pathKey,path);

            //uploadSize+=file.getSize();
            //redisTemplate.opsForValue().set(uploadSizeKey,String.valueOf(uploadSize));

            //将已经上上传成功的片数存储到redis中
            redisTemplate.opsForValue().set(uploadNoKey,"1");
        }else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if(StringUtils.isNullOrEmpty(filePath)){
                throw new ConditionException("上传失败");
            }
            //偏移量就是Redis中存储的分片大小
            this.modifyAppenderFile(file,filePath,uploadSize);
            //上传成功后仍然需要对Redis中的数据进行更新
            redisTemplate.opsForValue().increment(uploadNoKey);

            //uploadSize+=file.getSize();
            //redisTemplate.opsForValue().set(uploadSizeKey,String.valueOf(uploadSize));
        }
        uploadSize+=file.getSize();
        redisTemplate.opsForValue().set(uploadSizeKey,String.valueOf(uploadSize));

        //如果全部上传完毕的话，那么就要删除Redis中的key和value,并且返回给前端完整路径
        String uploaNoStr = redisTemplate.opsForValue().get(uploadNoKey);
        String resultPath="";
        Integer uploadNo=Integer.valueOf(uploaNoStr);
        if(uploadNo.equals(totalSliceNo)){
            //上传完毕
            resultPath=redisTemplate.opsForValue().get(pathKey);
            List<String>keyList= Arrays.asList(pathKey,uploadSizeKey,uploadNoKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }

    //文件分片
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);
        File file=this.multipartFileToFile(multipartFile);
        long fileLength = file.length();
        int count=1;
        for(int i=0;i<fileLength;i+=SLICE_SIZE){
            //支持文件位置随机访问
            RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
            //定位到文件分片的位置
            randomAccessFile.seek(i);
            byte[]bytes=new byte[SLICE_SIZE];
            //最后一个分片大小可能不足一个分片默认大小
            int len = randomAccessFile.read(bytes);
            String path="D:\\tempfile\\我是一个文件上传测试视频的第"+count+"."+fileType;
            File slice=new File(path);
            FileOutputStream fos=new FileOutputStream(slice);
            fos.write(bytes,0,len);
            randomAccessFile.close();
            count++;
        }
        //删除临时文件
        file.delete();
    }

    //将MultipartFile文件转为java中普通文件对象
    private File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        //生成一个临时文件
        File file=File.createTempFile(fileName[0],"."+fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    //删除
    public void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }
}
