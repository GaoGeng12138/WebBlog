package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.visitor.FindVisitorLogPageListReqVO;
import com.gaog.weblog.admin.service.AdminVisitorService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/visitor")
@Api(tags = "Admin 访客记录模块")
public class AdminVisitorController {

    @Autowired
    private AdminVisitorService adminVisitorService;

    @PostMapping("/list")
    @ApiOperation(value = "查询访客记录分页列表")
    @ApiOperationLog(description = "查询访客记录分页列表")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:visitor:list')")
    public PageResponse findVisitorLogPageList(@RequestBody @Validated FindVisitorLogPageListReqVO reqVO) {
        return adminVisitorService.findVisitorLogPageList(reqVO);
    }
}
