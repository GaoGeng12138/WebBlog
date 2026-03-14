package com.gaog.weblog.web.controller;


import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityRankingRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityStatisticsRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityTrendRspVO;
import com.gaog.weblog.web.service.UserActivityScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user/score")
@Api(tags = "User Activity Score Module")
public class UserActivityScoreController {
    
    @Autowired
    private UserActivityScoreService userActivityScoreService;
    
    @PostMapping("/activity-score")
    @ApiOperation(value = "Get user activity score")
    @ApiOperationLog(description = "Get user activity score")
    public Response<UserActivityScoreRspVO> getActivityScore(@Valid @RequestBody UserActivityScoreReqVO reqVO) {
        return userActivityScoreService.getActivityScore(reqVO);
    }
    
    @PostMapping("/activity-ranking")
    @ApiOperation(value = "Get user activity ranking")
    @ApiOperationLog(description = "Get user activity ranking")
    public PageResponse<UserActivityRankingRspVO> getActivityRanking(@Valid @RequestBody UserActivityRankingReqVO reqVO) {
        return userActivityScoreService.getActivityRanking(reqVO);
    }
    
    @GetMapping("/activity-statistics")
    @ApiOperation(value = "Get user activity statistics")
    @ApiOperationLog(description = "Get user activity statistics")
    public Response<UserActivityStatisticsRspVO> getActivityStatistics() {
        return userActivityScoreService.getActivityStatistics();
    }
    
    @GetMapping("/activity-trend")
    @ApiOperation(value = "Get user activity trend")
    @ApiOperationLog(description = "Get user activity trend")
    public Response<List<UserActivityTrendRspVO>> getActivityTrend(
            @RequestParam(required = false, defaultValue = "daily") String timeRange) {
        return userActivityScoreService.getUserActivityTrend(timeRange);
    }
}