package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.user.RegisterUserReqVO;
import com.gaog.weblog.web.model.vo.user.UserCurrentLocationRspVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;
import com.gaog.weblog.web.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @author Gao
 * @description 前台用户控制器
 */
@RestController
@RequestMapping("/user")
@Api(tags = "前台用户模块")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/info")
    @ApiOperation(value = "获取用户基本信息")
    @ApiOperationLog(description = "获取用户基本信息")
    public Response getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/stats")
    @ApiOperation(value = "获取用户中心统计数据")
    @ApiOperationLog(description = "获取用户中心统计数据")
    public Response getUserCenterStats() {
        return userService.getUserCenterStats();
    }

    @GetMapping("/location")
    @ApiOperation(value = "获取当前访问位置")
    @ApiOperationLog(description = "获取当前访问位置")
    public Response<UserCurrentLocationRspVO> getCurrentLocation(HttpServletRequest request) {
        return userService.getCurrentLocation(request);
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    @ApiOperationLog(description = "用户注册")
    public Response register(@RequestBody @Validated RegisterUserReqVO registerUserReqVO) {
        return userService.register(registerUserReqVO);
    }
    
    @GetMapping("/comment/history")
    @ApiOperation(value = "获取用户评论历史")
    @ApiOperationLog(description = "获取用户评论历史")
    public PageResponse getUserCommentHistory(@RequestParam @NotNull Long current,
                                              @RequestParam @NotNull Long size) {
        return userService.getUserCommentHistory(current, size);
    }
    
    @GetMapping("/dynamic")
    @ApiOperation(value = "获取用户动态")
    @ApiOperationLog(description = "获取用户动态")
    public PageResponse getUserDynamics(@RequestParam @NotNull Long current,
                                        @RequestParam @NotNull Long size) {
        return userService.getUserDynamics(current, size);
    }
    
    @PostMapping("/activity-score")
    @ApiOperation(value = "获取用户活跃度积分")
    @ApiOperationLog(description = "获取用户活跃度积分")
    public Response<UserActivityScoreRspVO> getUserActivityScore(@RequestBody @Validated UserActivityScoreReqVO reqVO) {
        return userService.getUserActivityScore(reqVO);
    }
}
