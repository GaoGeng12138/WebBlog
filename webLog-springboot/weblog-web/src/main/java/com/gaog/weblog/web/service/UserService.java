package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.user.RegisterUserReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;

/**
 * @author Gao
 * @description 前台用户服务接口
 */
public interface UserService {
    
    /**
     * 获取当前登录用户信息
     * @return
     */
    Response getUserInfo();

    /**
     * 获取用户中心统计数据（文章数、收藏数、评论数）
     * @return
     */
    Response getUserCenterStats();

    /**
     * 用户注册
     * @param registerUserReqVO
     * @return
     */
    Response register(RegisterUserReqVO registerUserReqVO);
    
    /**
     * 获取用户评论历史
     * @param current 当前页码
     * @param size 每页大小
     * @return
     */
    PageResponse getUserCommentHistory(Long current, Long size);
    
    /**
     * 获取用户动态
     * @param current 当前页码
     * @param size 每页大小
     * @return
     */
    PageResponse getUserDynamics(Long current, Long size);
    
    /**
     * 获取用户活跃度积分
     * @param reqVO 请求参数
     * @return
     */
    Response<UserActivityScoreRspVO> getUserActivityScore(UserActivityScoreReqVO reqVO);
}