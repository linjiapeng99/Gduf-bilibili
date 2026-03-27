package com.gduf.userservice.service;

import com.gduf.bilibilicommon.domain.Content;
import com.gduf.userservice.dao.ContentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    @Autowired
    private ContentDao contentDao;
    public Long addContent(Content content) {
        contentDao.addContent(content);
        return content.getId();
    }
}
