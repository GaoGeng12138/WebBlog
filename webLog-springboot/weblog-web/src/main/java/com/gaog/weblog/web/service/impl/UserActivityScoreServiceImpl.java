package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.CommentDO;
import com.gaog.weblog.common.domain.dos.UserActivityScoreDO;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.UserLikeCommentDO;
import com.gaog.weblog.common.domain.dos.UserFavoriteArticleDO;
import com.gaog.weblog.common.domain.dos.VisitorLogDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CommentMapper;
import com.gaog.weblog.common.domain.mapper.UserActivityScoreMapper;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.domain.mapper.UserLikeCommentMapper;
import com.gaog.weblog.common.domain.mapper.UserFavoriteArticleMapper;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.VisitorLogMapper;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityStatisticsRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityTrendRspVO;
import com.gaog.weblog.web.service.UserActivityScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserActivityScoreServiceImpl implements UserActivityScoreService {

    private static final String DEFAULT_ACTIVITY_SCORE_RULES = "[{\"type\":\"article\",\"name\":\"发布文章\",\"score\":10},{\"type\":\"comment\",\"name\":\"发表评论\",\"score\":2},{\"type\":\"favorite\",\"name\":\"收藏文章\",\"score\":1},{\"type\":\"like\",\"name\":\"点赞评论\",\"score\":1},{\"type\":\"login\",\"name\":\"每日登录\",\"score\":1}]";

    @Autowired
    private UserActivityScoreMapper userActivityScoreMapper;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserFavoriteArticleMapper userFavoriteArticleMapper;

    @Autowired
    private UserLikeCommentMapper userLikeCommentMapper;

    @Autowired
    private VisitorLogMapper visitorLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    public static class ActivityScoreRule {
        private String type;
        private String name;
        private Integer score;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }

    private List<ActivityScoreRule> resolveActivityScoreRules() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        String rawRules = setting != null ? setting.getActivityScoreRules() : null;

        try {
            String source = (rawRules == null || rawRules.trim().isEmpty()) ? DEFAULT_ACTIVITY_SCORE_RULES : rawRules;
            List<ActivityScoreRule> rules = objectMapper.readValue(source, new TypeReference<List<ActivityScoreRule>>() {});
            List<ActivityScoreRule> normalizedRules = new ArrayList<>();
            for (ActivityScoreRule rule : rules) {
                if (rule == null || rule.getType() == null || rule.getType().trim().isEmpty()) {
                    continue;
                }
                ActivityScoreRule normalizedRule = new ActivityScoreRule();
                normalizedRule.setType(rule.getType().trim());
                normalizedRule.setName(rule.getName() == null || rule.getName().trim().isEmpty() ? rule.getType().trim() : rule.getName().trim());
                normalizedRule.setScore(rule.getScore() == null ? 0 : Math.max(rule.getScore(), 0));
                normalizedRules.add(normalizedRule);
            }
            return normalizedRules.isEmpty() ? readDefaultActivityScoreRules() : normalizedRules;
        } catch (Exception e) {
            log.warn("Failed to parse activity score rules, fallback to default rules", e);
            return readDefaultActivityScoreRules();
        }
    }

    private List<ActivityScoreRule> readDefaultActivityScoreRules() {
        try {
            return objectMapper.readValue(DEFAULT_ACTIVITY_SCORE_RULES, new TypeReference<List<ActivityScoreRule>>() {});
        } catch (Exception e) {
            log.warn("Failed to load default activity score rules", e);
            return Collections.emptyList();
        }
    }

    private Map<String, ActivityScoreRule> toActivityScoreRuleMap(List<ActivityScoreRule> rules) {
        Map<String, ActivityScoreRule> ruleMap = new LinkedHashMap<>();
        for (ActivityScoreRule rule : rules) {
            if (rule != null && rule.getType() != null) {
                ruleMap.put(rule.getType(), rule);
            }
        }
        return ruleMap;
    }

    public void recalculateActivityScores(Long userId, LocalDate startDate, LocalDate endDate) {
        calculateAndSaveActivityScores(userId, startDate, endDate, toActivityScoreRuleMap(resolveActivityScoreRules()));
    }

    private int getRuleScore(Map<String, ActivityScoreRule> ruleMap, String type) {
        ActivityScoreRule rule = ruleMap.get(type);
        return rule != null && rule.getScore() != null ? rule.getScore() : 0;
    }

    @Override
    public Response<UserActivityScoreRspVO> getActivityScore(UserActivityScoreReqVO reqVO) {
        try {
            // Get user ID (either from request or current user)
            Long userId = SecurityContextUtil.getCurrentUserId();
            List<ActivityScoreRule> scoreRules = resolveActivityScoreRules();
            Map<String, ActivityScoreRule> ruleMap = toActivityScoreRuleMap(scoreRules);

            // Calculate date range based on timeRange parameter
            LocalDate[] dateRange = calculateDateRange(reqVO.getTimeRange());
            LocalDate startDate = dateRange[0];
            LocalDate endDate = dateRange[1];

            // Recalculate and persist the current range with the latest rule configuration
            calculateAndSaveActivityScores(userId, startDate, endDate, ruleMap);

            // Get activity data for the user in the specified date range
            List<UserActivityScoreDO> activityScores = getActivityScoresForUser(userId, startDate, endDate);

            // Calculate totals
            int totalScore = activityScores.stream().mapToInt(UserActivityScoreDO::getDailyScore).sum();
            int articleCount = activityScores.stream().mapToInt(UserActivityScoreDO::getArticleCount).sum();
            int commentCount = activityScores.stream().mapToInt(UserActivityScoreDO::getCommentCount).sum();
            int favoriteCount = activityScores.stream().mapToInt(UserActivityScoreDO::getFavoriteCount).sum();
            int likeCount = countUserCommentLikesInDateRange(userId, startDate, endDate);
            int loginCount = activityScores.stream().mapToInt(UserActivityScoreDO::getLoginCount).sum();

            Map<String, Integer> activityCountMap = new HashMap<>();
            activityCountMap.put("article", articleCount);
            activityCountMap.put("comment", commentCount);
            activityCountMap.put("favorite", favoriteCount);
            activityCountMap.put("like", likeCount);
            activityCountMap.put("login", loginCount);

            // Create activity details
            List<UserActivityScoreRspVO.ActivityDetailVO> activities = scoreRules.stream()
                    .map(rule -> UserActivityScoreRspVO.ActivityDetailVO.builder()
                            .type(rule.getType())
                            .name(rule.getName())
                            .count(activityCountMap.getOrDefault(rule.getType(), 0))
                            .score(activityCountMap.getOrDefault(rule.getType(), 0) * getRuleScore(ruleMap, rule.getType()))
                            .build())
                    .collect(Collectors.toList());

            // Create trend data
            List<UserActivityScoreRspVO.ScoreTrendVO> trend = activityScores.stream()
                    .map(score -> UserActivityScoreRspVO.ScoreTrendVO.builder()
                            .date(score.getActivityDate().toString())
                            .score(score.getDailyScore())
                            .build())
                    .collect(Collectors.toList());

            // Get user rank (simplified implementation)
            int rank = getUserRank(userId, startDate, endDate);
            int totalUsers = getTotalActiveUsers(startDate, endDate);

            // Build response
            UserActivityScoreRspVO rspVO = UserActivityScoreRspVO.builder()
                    .totalScore(totalScore)
                    .rank(rank)
                    .totalUsers(totalUsers)
                    .activities(activities)
                    .trend(trend)
                    .build();

            return Response.success(rspVO);
        } catch (Exception e) {
            log.error("Error getting user activity score: ", e);
            return Response.fail("Failed to get activity score");
        }
    }

    @Override
    public PageResponse<UserActivityRankingRspVO> getActivityRanking(UserActivityRankingReqVO reqVO) {
        try {
            // Calculate date range based on timeRange parameter
            LocalDate[] dateRange = calculateDateRange(reqVO.getTimeRange());
            LocalDate startDate = dateRange[0];
            LocalDate endDate = dateRange[1];

            // Get all users with their total scores in the date range
            Map<Long, Integer> userScores = getUserScoresInDateRange(startDate, endDate);

            // Convert to list and sort by score (descending)
            List<Map.Entry<Long, Integer>> sortedUserScores = userScores.entrySet().stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .collect(Collectors.toList());

            // Calculate total users
            int totalUsers = sortedUserScores.size();

            // Pagination
            int page = reqVO.getPage();
            int size = reqVO.getSize();
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, totalUsers);

            // Apply pagination to sorted list
            List<Map.Entry<Long, Integer>> pagedUserScores = new ArrayList<>();
            if (fromIndex < totalUsers) {
                pagedUserScores = sortedUserScores.subList(fromIndex, toIndex);
            }

            // Build response data
            List<UserActivityRankingRspVO> rankingList = new ArrayList<>();
            for (int i = 0; i < pagedUserScores.size(); i++) {
                Map.Entry<Long, Integer> entry = pagedUserScores.get(i);
                Long userId = entry.getKey();
                Integer score = entry.getValue();
                int rank = fromIndex + i + 1;

                // Get user details
                UserDO user = userMapper.selectById(userId);
                if (user != null) {
                    UserActivityRankingRspVO rankingVO = UserActivityRankingRspVO.builder()
                            .userId(userId)
                            .username(user.getUsername())
                            .avatar(user.getAvatar())
                            .score(score)
                            .rank(rank)
                            .build();
                    rankingList.add(rankingVO);
                }
            }

            // Create page object
            Page<UserActivityRankingRspVO> pageObj = new Page<>(page, size);
            pageObj.setTotal(totalUsers);
            pageObj.setRecords(rankingList);

            return PageResponse.success(pageObj, rankingList);
        } catch (Exception e) {
            log.error("Error getting user activity ranking: ", e);
            return PageResponse.failed("Failed to get activity ranking");
        }
    }

    /**
     * 获取用户活跃度趋势
     *
     * @param timeRange 时间范围: daily, weekly, monthly, yearly
     * @return
     */
    @Override
    public Response<List<UserActivityTrendRspVO>> getUserActivityTrend(String timeRange) {
        try {

            Long userId = SecurityContextUtil.getCurrentUserId();

            List<ActivityScoreRule> scoreRules = resolveActivityScoreRules();
            Map<String, ActivityScoreRule> ruleMap = toActivityScoreRuleMap(scoreRules);

            // Calculate date range based on timeRange parameter
            LocalDate[] dateRange = calculateDateRange(timeRange);
            LocalDate startDate = dateRange[0];
            LocalDate endDate = dateRange[1];

            // Recalculate and persist the current range with the latest rule configuration
            calculateAndSaveActivityScores(userId, startDate, endDate, ruleMap);

            // Get activity data for the user in the specified date range
            List<UserActivityScoreDO> activityScores = getActivityScoresForUser(userId, startDate, endDate);

            // Create trend data based on time range
            List<UserActivityTrendRspVO> trend;
            switch (timeRange.toLowerCase()) {
                case "week":
                    trend = aggregateByDay(activityScores);
                    break;
                case "month":
                    trend = aggregateByDay(activityScores);
                    break;
                case "year":
                    trend = aggregateByMonth(activityScores);
                    break;
                case "day":
                default:
                    trend = aggregateByDay(activityScores);
                    break;
            }

            return Response.success(trend);
        } catch (Exception e) {
            log.error("Error getting user activity trend: ", e);
            return Response.fail("Failed to get activity trend");
        }
    }

    /**
     * Calculate date range based on time range parameter
     */
    private LocalDate[] calculateDateRange(String timeRange) {
        LocalDate today = LocalDate.now();

        switch (timeRange.toLowerCase()) {
            case "day":
                // 近一周：显示最近7天的数据
                LocalDate weekStart = today.minusDays(6); // 7天包括今天
                return new LocalDate[]{weekStart, today};
            case "week":
                // 近一周：显示最近7天的数据
                LocalDate weekStart2 = today.minusDays(6); // 7天包括今天
                return new LocalDate[]{weekStart2, today};
            case "month":
                // 近一个月：显示最近30天的数据
                LocalDate monthStart = today.minusDays(29); // 30天包括今天
                return new LocalDate[]{monthStart, today};
            case "year":
                // 近一年：从去年同期到今天的完整时间段
                LocalDate yearStart = today.minusYears(1); // 去年今天
                LocalDate yearEnd = today;
                return new LocalDate[]{yearStart, yearEnd};
            case "all":
            default:
                // For "all", we'll use a reasonable period (e.g., last 30 days)
                LocalDate startDate = today.minusDays(30);
                return new LocalDate[]{startDate, today};
        }
    }

    /**
     * Get activity scores for a user within a date range
     */
    private List<UserActivityScoreDO> getActivityScoresForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserActivityScoreDO::getUserId, userId)
                .ge(UserActivityScoreDO::getActivityDate, startDate)
                .le(UserActivityScoreDO::getActivityDate, endDate)
                .orderByAsc(UserActivityScoreDO::getActivityDate);

        return userActivityScoreMapper.selectList(wrapper);
    }

    /**
     * Calculate and save activity scores for a user within a date range
     */
    private void calculateAndSaveActivityScores(Long userId, LocalDate startDate, LocalDate endDate, Map<String, ActivityScoreRule> ruleMap) {
        // Iterate through each day in the date range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Check if score already exists for this date
            LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserActivityScoreDO::getUserId, userId)
                    .eq(UserActivityScoreDO::getActivityDate, currentDate);

            UserActivityScoreDO existingScore = userActivityScoreMapper.selectOne(wrapper);

            // Recalculate and upsert every time to keep data aligned with latest rules
            int articleCount = countUserArticlesOnDate(userId, currentDate);
            int commentCount = countUserCommentsOnDate(userId, currentDate);
            int favoriteCount = countUserFavoritesOnDate(userId, currentDate);
            int likeCount = countUserCommentLikesOnDate(userId, currentDate);
            int loginCount = countUserLoginsOnDate(userId, currentDate);

            int dailyScore = (articleCount * getRuleScore(ruleMap, "article")) +
                    (commentCount * getRuleScore(ruleMap, "comment")) +
                    (favoriteCount * getRuleScore(ruleMap, "favorite")) +
                    (likeCount * getRuleScore(ruleMap, "like")) +
                    (loginCount * getRuleScore(ruleMap, "login"));

            if (existingScore == null) {
                UserActivityScoreDO activityScore = UserActivityScoreDO.builder()
                        .userId(userId)
                        .activityDate(currentDate)
                        .dailyScore(dailyScore)
                        .articleCount(articleCount)
                        .commentCount(commentCount)
                        .favoriteCount(favoriteCount)
                        .loginCount(loginCount)
                        .createTime(java.time.LocalDateTime.now())
                        .updateTime(java.time.LocalDateTime.now())
                        .build();

                userActivityScoreMapper.insert(activityScore);
            } else {
                existingScore.setDailyScore(dailyScore);
                existingScore.setArticleCount(articleCount);
                existingScore.setCommentCount(commentCount);
                existingScore.setFavoriteCount(favoriteCount);
                existingScore.setLoginCount(loginCount);
                existingScore.setUpdateTime(java.time.LocalDateTime.now());
                userActivityScoreMapper.updateById(existingScore);
            }

            currentDate = currentDate.plusDays(1);
        }
    }


    /**
     * Count articles published by user on a specific date
     */
    private int countUserArticlesOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false)
                .apply("DATE(create_time) = '" + date.toString() + "'");

        return Math.toIntExact(articleMapper.selectCount(wrapper));
    }

    /**
     * Count comments made by user on a specific date
     */
    private int countUserCommentsOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentDO::getUserId, userId)
                .eq(CommentDO::getIsDeleted, false)
                .apply("DATE(create_time) = '" + date.toString() + "'");

        return Math.toIntExact(commentMapper.selectCount(wrapper));
    }

    /**
     * Count favorites made by user on a specific date
     */
    private int countUserFavoritesOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<UserFavoriteArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteArticleDO::getUserId, userId)
                .apply("DATE(create_time) = '" + date.toString() + "'");

        return Math.toIntExact(userFavoriteArticleMapper.selectCount(wrapper));
    }

    /**
     * Count comment likes made by user on a specific date
     */
    private int countUserCommentLikesOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<UserLikeCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLikeCommentDO::getUserId, userId)
                .ge(UserLikeCommentDO::getCreateTime, date.atStartOfDay())
                .le(UserLikeCommentDO::getCreateTime, date.atTime(LocalTime.MAX));

        return Math.toIntExact(userLikeCommentMapper.selectCount(wrapper));
    }

    /**
     * Count comment likes made by user within a date range
     */
    private int countUserCommentLikesInDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<UserLikeCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLikeCommentDO::getUserId, userId)
                .ge(UserLikeCommentDO::getCreateTime, startDate.atStartOfDay())
                .le(UserLikeCommentDO::getCreateTime, endDate.atTime(LocalTime.MAX));

        return Math.toIntExact(userLikeCommentMapper.selectCount(wrapper));
    }

    /**
     * Count logins by user on a specific date
     */
    private int countUserLoginsOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<VisitorLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitorLogDO::getUserId, userId)
                .eq(VisitorLogDO::getVisitDate, date);

        return Math.toIntExact(visitorLogMapper.selectCount(wrapper));
    }

    /**
     * Get user rank based on total score in the date range
     */
    private int getUserRank(Long userId, LocalDate startDate, LocalDate endDate) {
        // This is a simplified implementation
        // In a production environment, you might want to optimize this with a more efficient query
        LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(UserActivityScoreDO::getActivityDate, startDate)
                .le(UserActivityScoreDO::getActivityDate, endDate);

        List<UserActivityScoreDO> allScores = userActivityScoreMapper.selectList(wrapper);

        // Group by user and sum scores
        Map<Long, Integer> userScores = new HashMap<>();
        for (UserActivityScoreDO score : allScores) {
            userScores.merge(score.getUserId(), score.getDailyScore(), Integer::sum);
        }

        // Sort users by score (descending)
        List<Map.Entry<Long, Integer>> sortedUsers = userScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Find user rank
        for (int i = 0; i < sortedUsers.size(); i++) {
            if (sortedUsers.get(i).getKey().equals(userId)) {
                return i + 1; // Rank is 1-indexed
            }
        }

        return sortedUsers.size() + 1; // User not found, place at the end
    }

    /**
     * Get total number of active users in the date range
     * This method ensures user deduplication by grouping by userId
     */
    private int getTotalActiveUsers(LocalDate startDate, LocalDate endDate) {
        // First, get all activity scores in the date range
        LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(UserActivityScoreDO::getActivityDate, startDate)
                .le(UserActivityScoreDO::getActivityDate, endDate);
        
        List<UserActivityScoreDO> allScores = userActivityScoreMapper.selectList(wrapper);
        
        // Then, deduplicate by userId using a Set
        Set<Long> uniqueUserIds = allScores.stream()
                .map(UserActivityScoreDO::getUserId)
                .collect(Collectors.toSet());
        
        return uniqueUserIds.size();
    }

    /**
     * Get all users with their total scores in the date range
     */
    private Map<Long, Integer> getUserScoresInDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(UserActivityScoreDO::getActivityDate, startDate)
                .le(UserActivityScoreDO::getActivityDate, endDate);

        List<UserActivityScoreDO> allScores = userActivityScoreMapper.selectList(wrapper);

        // Group by user and sum scores
        Map<Long, Integer> userScores = new HashMap<>();
        for (UserActivityScoreDO score : allScores) {
            userScores.merge(score.getUserId(), score.getDailyScore(), Integer::sum);
        }

        return userScores;
    }

    /**
     * Get user activity statistics
     */
    @Override
    public Response<UserActivityStatisticsRspVO> getActivityStatistics() {
        try {
            // Calculate date ranges
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());

            // Get daily average (last 7 days)
            LocalDate weekAgo = today.minusDays(7);
            Map<Long, Integer> weeklyScores = getUserScoresInDateRange(weekAgo, today);
            int dailyAverage = weeklyScores.isEmpty() ? 0 : weeklyScores.values().stream().mapToInt(Integer::intValue).sum() / 7;

            // Get weekly average (last 4 weeks)
            LocalDate monthAgo = today.minusDays(30);
            Map<Long, Integer> monthlyScores = getUserScoresInDateRange(monthAgo, today);
            int weeklyAverage = monthlyScores.isEmpty() ? 0 : monthlyScores.values().stream().mapToInt(Integer::intValue).sum() / 4;

            // Get monthly average (last 12 months)
            LocalDate yearAgo = today.minusDays(365);
            Map<Long, Integer> yearlyScores = getUserScoresInDateRange(yearAgo, today);
            int monthlyAverage = yearlyScores.isEmpty() ? 0 : yearlyScores.values().stream().mapToInt(Integer::intValue).sum() / 12;

            // Get best day in the last 30 days
            Map<LocalDate, Integer> dailyTotals = new HashMap<>();
            LambdaQueryWrapper<UserActivityScoreDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(UserActivityScoreDO::getActivityDate, monthAgo);
            List<UserActivityScoreDO> scores = userActivityScoreMapper.selectList(wrapper);

            for (UserActivityScoreDO score : scores) {
                dailyTotals.merge(score.getActivityDate(), score.getDailyScore(), Integer::sum);
            }

            String bestDay = "";
            int bestDayScore = 0;
            if (!dailyTotals.isEmpty()) {
                Map.Entry<LocalDate, Integer> bestDayEntry = dailyTotals.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);
                if (bestDayEntry != null) {
                    bestDay = bestDayEntry.getKey().toString();
                    bestDayScore = bestDayEntry.getValue();
                }
            }

            // Build response
            UserActivityStatisticsRspVO rspVO = UserActivityStatisticsRspVO.builder()
                    .dailyAverage(dailyAverage)
                    .weeklyAverage(weeklyAverage)
                    .monthlyAverage(monthlyAverage)
                    .bestDay(bestDay)
                    .bestDayScore(bestDayScore)
                    .build();

            return Response.success(rspVO);
        } catch (Exception e) {
            log.error("Error getting user activity statistics: ", e);
            return Response.fail("Failed to get activity statistics");
        }
    }

    /**
     * 按天聚合数据
     */
    private List<UserActivityTrendRspVO> aggregateByDay(List<UserActivityScoreDO> activityScores) {
        return activityScores.stream()
                .map(score -> UserActivityTrendRspVO.builder()
                        .date(score.getActivityDate().toString())
                        .score(score.getDailyScore())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 按月聚合数据
     */
    private List<UserActivityTrendRspVO> aggregateByMonth(List<UserActivityScoreDO> activityScores) {
        // 按月分组并求和
        Map<String, Integer> monthlyScores = new LinkedHashMap<>();
        for (UserActivityScoreDO score : activityScores) {
            String month = score.getActivityDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyScores.merge(month, score.getDailyScore(), Integer::sum);
        }

        // 转换为结果列表
        return monthlyScores.entrySet().stream()
                .map(entry -> UserActivityTrendRspVO.builder()
                        .date(entry.getKey())
                        .score(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
