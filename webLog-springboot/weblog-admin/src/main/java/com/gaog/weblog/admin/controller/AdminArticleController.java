package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.article.AuditArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.DeleteArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticlePageListReqVO;
import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.admin.service.AdminArticleService;
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
 * @author ZSJ
 * @date 2025/11/25 16:16
 * @description
 */
@RestController
@RequestMapping("/admin/article")
@Api(tags = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService articleService;

    @PostMapping("/publish")
    @ApiOperation(value = "文章发布")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:publish')")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "文章删除")
    @ApiOperationLog(description = "文章删除")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:delete')")
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "文章分页数据获取")
    @ApiOperationLog(description = "文章分页数据获取")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:list')")
    public PageResponse findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "文章详情获取")
    @ApiOperationLog(description = "文章详情获取")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:list','admin:article:update')")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "文章更新")
    @ApiOperationLog(description = "文章更新")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:update')")
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/audit")
    @ApiOperation(value = "文章审核")
    @ApiOperationLog(description = "文章审核")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:audit')")
    public Response auditArticle(@RequestBody @Validated AuditArticleReqVO aditArticleReqVO) {
        return articleService.auditArticle(aditArticleReqVO);
    }

}
