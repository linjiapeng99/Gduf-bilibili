package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDao {
    File getFileByMd5(String fileMd5);

    void addFile(File file);
}
