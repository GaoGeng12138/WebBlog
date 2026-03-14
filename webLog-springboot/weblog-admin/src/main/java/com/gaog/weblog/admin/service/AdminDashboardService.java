package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.dashboard.*;
import com.gaog.weblog.common.utils.Response;
import java.util.List;

/**
 * @author ZSJ
 * @date 2025/12/2 10:50
 * @description Dashboard service interface
 */
public interface AdminDashboardService {

    /**
     * 获取仪表盘统计数据
     * @return
     */
    Response<DashboardStatsRspVO> getDashboardStats();

    /**
     * 获取文章发布趋势
     * @return
     */
    Response<List<ArticlePublishTrendRspVO>> getArticlePublishTrend();

    /**
     * 获取网站访问量趋势
     * @return
     */
    Response<List<PvTrendRspVO>> getPvTrend();

    /**
     * 获取用户活跃度趋势
     * @return
     */
    Response<List<UserActivityTrendRspVO>> getUserActivityTrend(UserActivityTrendReqVO reqVO);

    /**
     * 获取最新文章列表
     * @return
     */
    Response<List<LatestArticleRspVO>> getLatestArticles();
}