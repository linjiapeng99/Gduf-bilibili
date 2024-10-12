package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCoinService {
    @Autowired
    private UserCoinDao userCoinDao;

    public Integer getUserCoinAmount(Long userId) {
       return userCoinDao.getUserCoinAmount(userId);
    }

    public void updateUserCoinAmount(Long userId, Integer amount) {
        Date updateTime=new Date();
        userCoinDao.updateUserCoinAmount(userId,amount,updateTime);
    }
}
