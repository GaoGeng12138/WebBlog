package com.gaog.weblog.web.controller;

import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.article.FindArticleByCategoryReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleByTagReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.gaog.weblog.web.model.vo.article.FrontendUpdateArticleReqVO;
import com.gaog.weblog.web.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:22
 * @Version: 1.0
 * @Description:
 */
@RestController
@Api(tags = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/article/list")
    @ApiOperation(value = "获取首页文章分页数据（支持按用户筛选）")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public Response findArticlePageList(@RequestBody @Valid FindIndexArticlePageListReqVO findIndexArticlePageListReqVO,
                                         HttpServletRequest request) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO, request);
    }

    @PostMapping("/article/list/by-category")
    @ApiOperation(value = "根据分类ID获取文章分页数据")
    @ApiOperationLog(description = "根据分类ID获取文章分页数据")
    public PageResponse findArticlePageListByCategoryId(@RequestBody @Valid FindArticleByCategoryReqVO findArticleByCategoryReqVO) {
        return articleService.findArticlePageListByCategoryId(findArticleByCategoryReqVO);
    }

    @PostMapping("/article/list/by-tag")
    @ApiOperation(value = "根据标签ID获取文章分页数据")
    @ApiOperationLog(description = "根据标签ID获取文章分页数据")
    public PageResponse findArticlePageListByTagId(@RequestBody @Valid FindArticleByTagReqVO findArticleByTagReqVO) {
        return articleService.findArticlePageListByTagId(findArticleByTagReqVO);
    }

    @PostMapping("/article/detail")
    @ApiOperation(value = "获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody @Valid FindArticleDetailReqVO findArticleDetailReqVO,
                                      HttpServletRequest request) {
        return articleService.findArticleDetail(findArticleDetailReqVO, request);
    }


    @PostMapping("/article/publish")
    @ApiOperation(value = "文章发布")
    @ApiOperationLog(description = "文章发布")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/article/update")
    @ApiOperation(value = "文章更新")
    @ApiOperationLog(description = "文章更新")
    public Response updateArticle(@RequestBody @Validated FrontendUpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }
}
