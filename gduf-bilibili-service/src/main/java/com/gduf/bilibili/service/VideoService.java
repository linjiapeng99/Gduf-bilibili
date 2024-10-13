package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.UserCoinDao;
import com.gduf.bilibili.dao.VideoDao;
import com.gduf.bilibili.domain.*;
import com.gduf.bilibili.exception.ConditionException;
import com.gduf.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private CollectionGroupService collectionGroupService;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private UserCoinDao userCoinDao;
    @Autowired
    private UserService userService;
    @Autowired
    /**
     * 添加视频
     *
     * @param video
     */
    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideos(video);
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setVideoId(videoId);
            item.setCreateTime(now);
        });
        videoDao.batchAddVideoTags(tagList);
    }

    /**
     * 视频分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param area
     * @return
     */
    public PageResult<Video> pageListVideos(Integer pageNo, Integer pageSize, String area) {
        if (pageNo == null || pageSize == null) {
            throw new ConditionException("参数异常");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (pageNo - 1) * pageSize);
        params.put("limit", pageSize);
        params.put("area", area);
        List<Video> list = new ArrayList<>();
        Integer total = videoDao.pageCountVideo(params);
        if (total > 0) {
            list = videoDao.pageListVideos(params);
            List<VideoTag> tagList = new ArrayList<>();
            for (Video video : list) {
                Long videoId = video.getId();
                tagList = videoDao.getVideoTagsByVideoId(videoId);
                video.setVideoTagList(tagList);
            }
        }
        return new PageResult<>(total, list);
    }

    /**
     * 视频在线下载
     *
     * @param request
     * @param response
     * @param path
     * @throws Exception
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, path);
    }

    /**
     * 视频点赞
     *
     * @param userId  点赞视频的用户，即当前登录用户
     * @param videoId 点赞的视频id
     */
    public void addVideoLike(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        if (videoLike != null) {
            throw new ConditionException("已经赞过");
        }
        videoLike = new VideoLike();
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long userId, Long videoId) {
        videoDao.deleteVideoLike(userId, videoId);
    }

    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        //获取视频点赞数量
        Long count = videoDao.getVideoLikesByVideoId(videoId);
        //如果userId确实存在，那么就查询当前用户对该视频是否进行了点赞
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        Boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    @Transactional
    public void addVideoCollection(Long userId, VideoCollection videoCollection) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        //删除原有收藏
        videoDao.deleteVideoCollection(userId, videoId);
        //新增收藏
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long userId, Long videoId) {
        videoDao.deleteVideoCollection(userId, videoId);
    }

    public Map<String, Object> getVideoCollections(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Long count = videoDao.getVideoCollectionsByVideoId(videoId);
        //如果userId确实存在，那么就查询当前用户对该视频是否进行了点赞
        VideoLike videoLike = videoDao.getVideoCollectionByVideoIdAndUserId(userId, videoId);
        Boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    public Long addCollectionGroup(CollectionGroup collectionGroup) {
        return collectionGroupService.addCollectionGroup(collectionGroup);
    }

    public List<CollectionGroup> getCollectionGroups(Long userId) {
        return collectionGroupService.getCollectionGroups(userId);
    }

    @Transactional
    public void addVideoCoins(VideoCoin videoCoin) {
        //获取视频id和硬币数量
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if (videoId == null || amount == null) {
            throw new ConditionException("参数异常");
        }
        //获取视频，查看前端传来的是否是非法视频
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Long userId = videoCoin.getUserId();
        //获取用户的账户硬币数量
        Integer userCoinAmount = userCoinService.getUserCoinAmount(userId);
        userCoinAmount = userCoinAmount == null ? 0 : userCoinAmount;
        //如果投的硬币数量超过已有的，那么就提示错误
        if (amount > userCoinAmount) {
            throw new ConditionException("硬币数量不足");
        }
        //查询当前用户对该视频是否投过币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(userId, videoId);
        if (dbVideoCoin == null) {
            //新增投币记录
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoins(videoCoin);
        } else {
            //获取历史投币数量
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            //更新投币数量
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoins(videoCoin);
        }
        //更新用户的硬币余额
        userCoinService.updateUserCoinAmount(userId, (userCoinAmount - amount));
    }

    public Map<String, Object> getVideoCoins(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Long count = videoDao.getVideoCoinAmount(video);
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(userId, videoId);
        boolean like = videoCoin != null;
        Map<String, Object> result = new HashMap<>();
        result.put("like", like);
        result.put("count", count);
        return result;
    }

    public void addVideoComments(VideoComment videoComment, Long userId) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComments(videoComment);
    }

    public PageResult<VideoComment> pageListVideoComments(Integer pageNo, Integer pageSize, Long videoId) {
        //参数判断
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        //参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("start", (pageNo - 1) * pageSize);
        params.put("limit", pageSize);
        params.put("videoId", videoId);
        //查询一级评论总数量
        Integer total = videoDao.pageCountVideoComments(params);
        List<VideoComment> list = new ArrayList<>();
        if (total > 0) {
            //这里查的是一级评论
            list = videoDao.pageListVideoComments(params);
            //批量查询二级评论
            List<Long> parentIds = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(parentIds);
            //一级评论用户id
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            //二级评论用户id
            Set<Long> childIdSet = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            //二级评论回复用户id
            //Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            userIdList.addAll(childIdSet);
            List<UserInfo>userInfoList=userService.batchGetUserInfoByUserIds(userIdList);
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            list.forEach(comment->{
                Long id=comment.getId();
                List<VideoComment>childList=new ArrayList<>();
                childCommentList.forEach(child->{
                    if(id.equals(child.getRootId())){
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });
                comment.setChildList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }
        return new PageResult<>(total,list);
    }

    public Map<String, Object> getVideoDetail(Long videoId) {
        Video video=videoDao.getVideoById(videoId);
        Long userId = video.getUserId();
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String,Object>result=new HashMap<>();
        result.put("video",video);
        result.put("userInfo",userInfo);
        return result;
    }
}
