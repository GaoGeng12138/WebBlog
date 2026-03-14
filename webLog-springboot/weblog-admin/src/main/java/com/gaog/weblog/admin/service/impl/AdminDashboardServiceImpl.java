package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gaog.weblog.admin.model.vo.dashboard.*;
import com.gaog.weblog.admin.service.AdminDashboardService;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.dos.StatisticsArticlePvDO;
import com.gaog.weblog.common.domain.dos.TagDO;
import com.gaog.weblog.common.domain.dos.VisitorLogDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.domain.mapper.StatisticsArticlePvMapper;
import com.gaog.weblog.common.domain.mapper.TagMapper;
import com.gaog.weblog.common.domain.mapper.VisitorLogMapper;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZSJ
 * @date 2025/12/2 11:00
 * @description Dashboard service implementation
 */
@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private StatisticsArticlePvMapper statisticsArticlePvMapper;
    
    @Autowired
    private VisitorLogMapper visitorLogMapper;

    /**
     * 获取仪表盘统计数据
     *
     * @return
     */
    @Override
    public Response<DashboardStatsRspVO> getDashboardStats() {
        // 统计文章总数
        Long articleTotal = articleMapper.selectCount(null);

        // 统计分类总数
        Long categoryTotal = categoryMapper.selectCount(null);

        // 统计标签总数
        Long tagTotal = tagMapper.selectCount(null);

        // 统计总浏览量（独立访客总数）
        Long pvTotal = visitorLogMapper.selectCount(null);

        // 组装返回数据
        DashboardStatsRspVO dashboardStatsRspVO = DashboardStatsRspVO.builder()
                .articleTotal(articleTotal)
                .categoryTotal(categoryTotal)
                .tagTotal(tagTotal)
                .pvTotal(pvTotal)
                .build();

        return Response.success(dashboardStatsRspVO);
    }

    /**
     * 获取文章发布趋势
     *
     * @return
     */
    @Override
    public Response<List<ArticlePublishTrendRspVO>> getArticlePublishTrend() {
        // 查询最近7天每天的文章发布数量
        List<ArticlePublishTrendRspVO> publishCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        
        // 遍历最近7天 (从6天前到今天)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // 构造查询条件：查找创建日期等于指定日期的文章
            LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.apply("DATE(create_time) = '" + date.toString() + "'");

            // 查询当天发布的文章数量
            Long count = articleMapper.selectCount(wrapper);

            // 格式化日期为 yyyy-MM-dd
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到结果列表
            publishCounts.add(ArticlePublishTrendRspVO.builder()
                    .date(dateStr)  // 使用日期字符串
                    .count(count)
                    .build());
        }

        return Response.success(publishCounts);
    }

    /**
     * 获取网站访问量趋势
     *
     * @return
     */
    @Override
    public Response<List<PvTrendRspVO>> getPvTrend() {
        // 查询最近7天每天的浏览量
        List<PvTrendRspVO> pvCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        
        // 遍历最近7天 (从6天前到今天)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // 查询当天的PV统计记录
            LambdaQueryWrapper<StatisticsArticlePvDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StatisticsArticlePvDO::getPvDate, date);
            StatisticsArticlePvDO pvDO = statisticsArticlePvMapper.selectOne(wrapper);

            // 获取当天的浏览量
            Long pv = (pvDO != null && pvDO.getPvCount() != null) ? pvDO.getPvCount() : 0L;

            // 格式化日期为 yyyy-MM-dd
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到结果列表
            pvCounts.add(PvTrendRspVO.builder()
                    .date(dateStr)  // 使用日期字符串
                    .pv(pv)
                    .build());
        }

        return Response.success(pvCounts);
    }

    /**
     * 获取用户活跃度趋势
     *
     * @return
     */
    @Override
    public Response<List<UserActivityTrendRspVO>> getUserActivityTrend(UserActivityTrendReqVO reqVO) {
        String timeRange = reqVO.getTimeRange();
        
        switch (timeRange) {
            case "week":
                return getUserActivityTrendWeekly();
            case "month":
                return getUserActivityTrendMonthly();
            case "year":
                return getUserActivityTrendYearly();
            case "day":
            default:
                return getUserActivityTrendDaily();
        }
    }

    /**
     * 获取日活跃度趋势（最近7天）
     */
    private Response<List<UserActivityTrendRspVO>> getUserActivityTrendDaily() {
        // 查询最近7天每天的活跃用户数
        List<UserActivityTrendRspVO> activeUserCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 7天包括今天
        
        // 遍历最近7天 (从开始日期到今天)
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);

            // 查询当天有活动的独立用户数（通过visitor_log表统计）
            // 先查询出当天的所有记录，然后在内存中去重统计
            LambdaQueryWrapper<VisitorLogDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.apply("DATE(create_time) = '" + date.toString() + "'");
            
            List<VisitorLogDO> visitorLogs = visitorLogMapper.selectList(queryWrapper);
            
            // 使用Set统计不同的IP地址数量
            Set<String> uniqueIps = visitorLogs.stream()
                .map(VisitorLogDO::getIpAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Long activeCount = (long) uniqueIps.size();

            // 格式化日期为 yyyy-MM-dd
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到结果列表
            activeUserCounts.add(UserActivityTrendRspVO.builder()
                    .date(dateStr)  // 使用日期字符串
                    .activeUserCount(activeCount)
                    .build());
        }

        return Response.success(activeUserCounts);
    }

    /**
     * 获取周活跃度趋势（最近7天，按天显示）
     */
    private Response<List<UserActivityTrendRspVO>> getUserActivityTrendWeekly() {
        // 查询最近7天每天的活跃用户数
        List<UserActivityTrendRspVO> activeUserCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 7天包括今天
        
        // 遍历最近7天 (从开始日期到今天)
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);

            // 查询当天有活动的独立用户数（通过visitor_log表统计）
            LambdaQueryWrapper<VisitorLogDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.apply("DATE(create_time) = '" + date.toString() + "'");
            
            List<VisitorLogDO> visitorLogs = visitorLogMapper.selectList(queryWrapper);
            
            // 使用Set统计不同的IP地址数量
            Set<String> uniqueIps = visitorLogs.stream()
                .map(VisitorLogDO::getIpAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Long activeCount = (long) uniqueIps.size();

            // 格式化日期为 yyyy-MM-dd
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到结果列表
            activeUserCounts.add(UserActivityTrendRspVO.builder()
                    .date(dateStr)
                    .activeUserCount(activeCount)
                    .build());
        }

        return Response.success(activeUserCounts);
    }

    /**
     * 获取月活跃度趋势（最近30天，按天显示）
     */
    private Response<List<UserActivityTrendRspVO>> getUserActivityTrendMonthly() {
        // 查询最近30天每天的活跃用户数
        List<UserActivityTrendRspVO> activeUserCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29); // 30天包括今天
        
        // 遍历最近30天
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);

            // 查询当天有活动的独立用户数（通过visitor_log表统计）
            LambdaQueryWrapper<VisitorLogDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.apply("DATE(create_time) = '" + date.toString() + "'");
            
            List<VisitorLogDO> visitorLogs = visitorLogMapper.selectList(queryWrapper);
            
            // 使用Set统计不同的IP地址数量
            Set<String> uniqueIps = visitorLogs.stream()
                .map(VisitorLogDO::getIpAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Long activeCount = (long) uniqueIps.size();

            // 格式化日期为 yyyy-MM-dd
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到结果列表
            activeUserCounts.add(UserActivityTrendRspVO.builder()
                    .date(dateStr)
                    .activeUserCount(activeCount)
                    .build());
        }

        return Response.success(activeUserCounts);
    }

    /**
     * 获取年活跃度趋势（最近12个月，按月显示）
     */
    private Response<List<UserActivityTrendRspVO>> getUserActivityTrendYearly() {
        // 查询最近12个月每月的活跃用户数
        List<UserActivityTrendRspVO> activeUserCounts = new ArrayList<>();

        // 获取当前日期
        LocalDate today = LocalDate.now();
        
        // 遍历最近12个月
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1); // 每月第一天
            LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth()); // 每月最后一天

            // 查询这个月有活动的独立用户数
            LambdaQueryWrapper<VisitorLogDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.apply("DATE(create_time) BETWEEN '" + monthStart.toString() + "' AND '" + monthEnd.toString() + "'");
            
            List<VisitorLogDO> visitorLogs = visitorLogMapper.selectList(queryWrapper);
            
            // 使用Set统计不同的IP地址数量
            Set<String> uniqueIps = visitorLogs.stream()
                .map(VisitorLogDO::getIpAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            Long activeCount = (long) uniqueIps.size();

            // 格式化日期为 yyyy-MM (显示月份)
            String dateStr = monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // 添加到结果列表
            activeUserCounts.add(UserActivityTrendRspVO.builder()
                    .date(dateStr)
                    .activeUserCount(activeCount)
                    .build());
        }

        return Response.success(activeUserCounts);
    }

    /**
     * 获取最新文章列表
     *
     * @return
     */
    @Override
    public Response<List<LatestArticleRspVO>> getLatestArticles() {
        // 构造查询条件，按创建时间倒序排列，取前10条
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ArticleDO::getCreateTime);
        wrapper.last("LIMIT 10");

        // 查询最新文章
        List<ArticleDO> articles = articleMapper.selectList(wrapper);

        // 转换为VO对象
        List<LatestArticleRspVO> latestArticles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(articles)) {
            latestArticles = articles.stream()
                    .map(article -> LatestArticleRspVO.builder()
                            .id(article.getId())
                            .title(article.getTitle())
                            .createTime(article.getCreateTime())
                            .readNum(article.getReadNum())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(latestArticles);
    }
}