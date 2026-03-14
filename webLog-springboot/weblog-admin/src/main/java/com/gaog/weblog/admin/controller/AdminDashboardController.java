package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.dashboard.ArticlePublishTrendRspVO;
import com.gaog.weblog.admin.model.vo.dashboard.DashboardStatsRspVO;
import com.gaog.weblog.admin.model.vo.dashboard.LatestArticleRspVO;
import com.gaog.weblog.admin.model.vo.dashboard.PvTrendRspVO;
import com.gaog.weblog.admin.model.vo.dashboard.UserActivityTrendReqVO;
import com.gaog.weblog.admin.model.vo.dashboard.UserActivityTrendRspVO;
import com.gaog.weblog.admin.service.AdminDashboardService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ZSJ
 * @date 2025/12/2 14:30
 * @description Admin Dashboard Controller
 */
@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin 仪表盘模块")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/stats")
    @ApiOperation(value = "获取仪表盘统计信息")
    @ApiOperationLog(description = "获取仪表盘统计信息")
    public Response<DashboardStatsRspVO> getDashboardStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/article-publish-trend")
    @ApiOperation(value = "获取文章发布趋势")
    @ApiOperationLog(description = "获取文章发布趋势")
    public Response<List<ArticlePublishTrendRspVO>> getArticlePublishTrend() {
        return dashboardService.getArticlePublishTrend();
    }

    @GetMapping("/pv-trend")
    @ApiOperation(value = "获取网站访问量趋势")
    @ApiOperationLog(description = "获取网站访问量趋势")
    public Response<List<PvTrendRspVO>> getPvTrend() {
        return dashboardService.getPvTrend();
    }

    @PostMapping("/user-activity-trend")
    @ApiOperation(value = "获取用户活跃度趋势")
    @ApiOperationLog(description = "获取用户活跃度趋势")
    public Response<List<UserActivityTrendRspVO>> getUserActivityTrend(@RequestBody UserActivityTrendReqVO reqVO) {
        return dashboardService.getUserActivityTrend(reqVO);
    }

    @GetMapping("/latest-articles")
    @ApiOperation(value = "获取最新文章列表")
    @ApiOperationLog(description = "获取最新文章列表")
    public Response<List<LatestArticleRspVO>> getLatestArticles() {
        return dashboardService.getLatestArticles();
    }
}