package com.gduf.bilibili.service.config;

import com.github.tobato.fastdfs.domain.fdfs.StorageNode;
import com.github.tobato.fastdfs.domain.fdfs.StorageNodeInfo;
import com.github.tobato.fastdfs.service.DefaultTrackerClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("UserTrackerClient")
public class UserDefaultTrackerClient extends DefaultTrackerClient {
    @Override
    public StorageNode getStoreStorage(String groupName) {
        StorageNode result=super.getStoreStorage(groupName);
        result.setPort(23000);
        return result;
    }

    @Override
    public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
        StorageNodeInfo result=super.getUpdateStorage(groupName,filename);
        result.setPort(23000);
        return result;
    }
}
