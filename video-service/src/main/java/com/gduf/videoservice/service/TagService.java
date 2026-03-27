package com.gduf.videoservice.service;

import com.gduf.bilibilicommon.domain.Tag;
import com.gduf.videoservice.dao.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagDao tagDao;

    public Long addTags(Tag tag) {
        tag.setCreateTime(new Date());
        tagDao.addTags(tag);
        return tag.getId();
    }

    public List<Tag> getTags(String name) {
        return tagDao.getTags(name);
    }

    public void deleteTags(Tag tag) {
        tagDao.deleteTags(tag);
    }
}
