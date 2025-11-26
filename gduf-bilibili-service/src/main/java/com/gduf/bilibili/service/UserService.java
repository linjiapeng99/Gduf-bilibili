package com.gduf.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.dao.UserDao;
import com.gduf.bilibili.domain.PageResult;
import com.gduf.bilibili.domain.RefreshTokenDetail;
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

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAuthService userAuthService;

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
        //添加用户默认角色
        userAuthService.addUserDefaultRole(user.getId());
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
            //对密码进行解密
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String salt = dbUser.getSalt();
        //将前端传来的密码变成md5标识，再和数据库中的密码对比
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
     * 根据手机号或者邮箱查询用户
     *
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
     *
     * @param userInfo
     */
    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfo(userInfo);
    }

    /**
     * 根据用户id获取用户
     * @param followingId
     * @return
     */
    public User getById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIds) {
        return userDao.getUserInfoByUserIds(userIds);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject param) {
        Integer pageNum = param.getInteger("pageNum");
        Integer pageSize = param.getInteger("pageSize");
        param.put("start", (pageNum - 1) * pageSize);
        param.put("limit", pageSize);
        Integer total = userDao.pageCountUserInfos(param);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(param);
        }
        return new PageResult<>(total, list);
    }

    /**
     * 双令牌登录
     *
     * @param user
     * @return
     */
    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数错误！");
        }
        //User dbUser = this.getUserByPhone(phone);
        String phoneOrEmail = phone + email;
        User dbUser = this.getUserByPhoneOrEmail(phoneOrEmail);
        String password = user.getPassword();
        //解密密码
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        //解完密的密码还要转为md5，然后和数据库中的密码对比
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        Long userId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //更新用户的刷新令牌，现删除再添加
        userDao.deleteRefreshToken(refreshToken, userId);
        userDao.addRefreshToken(refreshToken, userId, new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /**
     * 用户登出，删除数据库中的刷新令牌
     *
     * @param refreshToken
     * @param userId
     */
    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    /**
     * 刷新用户令牌
     *
     * @param refreshToken
     * @return
     */
    public String refreshToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if(refreshTokenDetail==null){
            throw new ConditionException("555","token过期");
        }
        Long userId = refreshTokenDetail.getUserId();
        String accessToken = TokenUtil.generateToken(userId);
        return accessToken;
    }

    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.batchGetUserInfoByUserIds(userIdList);
    }
}
