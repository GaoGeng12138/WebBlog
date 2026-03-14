package com.gaog.weblog.web.service;


import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityStatisticsRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityTrendRspVO;


import java.util.List;

/**
 * User Activity Score Service Interface
 */
public interface UserActivityScoreService {

    /**
     * Get user activity score
     *
     * @param reqVO request VO containing user ID and time range
     * @return Response with activity score details
     */
    Response<UserActivityScoreRspVO> getActivityScore(UserActivityScoreReqVO reqVO);

    /**
     * Get user activity ranking
     *
     * @param reqVO request VO containing pagination and time range
     * @return PageResponse with activity ranking data
     */
    PageResponse<UserActivityRankingRspVO> getActivityRanking(UserActivityRankingReqVO reqVO);

    /**
     * Get user activity statistics
     *
     * @return Response with activity statistics data
     */
    Response<UserActivityStatisticsRspVO> getActivityStatistics();

    /**
     * 获取用户活跃度趋势
     *
     * @param timeRange 时间范围: daily, weekly, monthly, yearly
     * @return
     */
    Response<List<UserActivityTrendRspVO>> getUserActivityTrend(String timeRange);
}