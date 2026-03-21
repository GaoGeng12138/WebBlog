package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.comment.*;
import com.gaog.weblog.admin.service.AdminCommentService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Comment Controller
 */
@RestController
@RequestMapping("/admin/comment")
@Api(tags = "Admin Comment Module")
public class AdminCommentController {

    @Autowired
    private AdminCommentService adminCommentService;

    @PostMapping("/list")
    @ApiOperation(value = "Get Comment Page List")
    @ApiOperationLog(description = "Get Comment Page List")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:comment:list')")
    public PageResponse findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO findCommentPageListReqVO) {
        return adminCommentService.findCommentPageList(findCommentPageListReqVO);
    }

    @PostMapping("/audit")
    @ApiOperation(value = "Audit Comment")
    @ApiOperationLog(description = "Audit Comment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:comment:audit')")
    public Response auditComment(@RequestBody @Validated AuditCommentReqVO auditCommentReqVO) {
        return adminCommentService.auditComment(auditCommentReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "Delete Comment")
    @ApiOperationLog(description = "Delete Comment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:comment:delete')")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return adminCommentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/top")
    @ApiOperation(value = "Set Comment Top")
    @ApiOperationLog(description = "Set Comment Top")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:comment:top')")
    public Response setCommentTop(@RequestBody @Validated SetCommentTopReqVO setCommentTopReqVO) {
        return adminCommentService.setCommentTop(setCommentTopReqVO);
    }
}
