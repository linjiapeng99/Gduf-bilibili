package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.UserDao;
import com.gduf.bilibili.domain.User;
import com.gduf.bilibili.domain.UserInfo;
import com.gduf.bilibili.domain.constant.UserContant;
import com.gduf.bilibili.exception.ConditionException;
import com.gduf.bilibili.service.util.MD5Util;
import com.gduf.bilibili.service.util.RSAUtil;
import com.gduf.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    /**
     * 用户注册
     *
     * @param user
     */
    public void addUser(User user) {
        String phone = user.getPhone();
        //校验手机号
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPasssword;
        try {
            rawPasssword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        String md5Password = MD5Util.sign(rawPasssword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserContant.DEFAULT_NICK);
        userInfo.setBirth(UserContant.DEFAULT_BIRTH);
        userInfo.setGender(UserContant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }

    /**
     * 根据手机号获取用户
     *
     * @param phone
     * @return
     */
    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     * @throws Exception
     */
    public String login(User user) throws Exception {
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数错误！");
        }
        //User dbUser = this.getUserByPhone(phone);
        String phoneOrEmail = phone + email;
        User dbUser = this.getUserByPhoneOrEmail(phoneOrEmail);
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
     * 根据手机号或者邮箱查询用户
     * @param phoneOrEmail
     * @return
     */
    public User getUserByPhoneOrEmail(String phoneOrEmail) {
        return userDao.getUserByPhoneOrEmail(phoneOrEmail);
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    public void updateUsers(User user) throws Exception {
        Long userId = user.getId();
        User dbUser = userDao.getUserById(userId);
        if (dbUser == null) {
            throw new ConditionException("用户不存在！");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }

    /**
     * 更新用户账户信息
     * @param userInfo
     */
    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfo(userInfo);
    }
}
