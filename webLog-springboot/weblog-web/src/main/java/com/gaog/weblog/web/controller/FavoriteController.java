package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.gaog.weblog.web.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/favorite")
@Api(tags = "收藏模块")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/article")
    @ApiOperation(value = "收藏文章")
    @ApiOperationLog(description = "收藏文章")
    public Response favoriteArticle(@RequestParam Long articleId) {
        return favoriteService.favoriteArticle(articleId);
    }

    @DeleteMapping("/article")
    @ApiOperation(value = "取消收藏文章")
    @ApiOperationLog(description = "取消收藏文章")
    public Response unfavoriteArticle(@RequestParam Long articleId) {
        return favoriteService.unfavoriteArticle(articleId);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取收藏文章列表")
    @ApiOperationLog(description = "获取收藏文章列表")
    public Response getFavoriteArticles(@RequestBody @Valid FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return favoriteService.getFavoriteArticles(findIndexArticlePageListReqVO.getCurrent(), findIndexArticlePageListReqVO.getSize());
    }

    @GetMapping("/check")
    @ApiOperation(value = "检查文章是否已收藏")
    @ApiOperationLog(description = "检查文章是否已收藏")
    public Response isFavorited(@RequestParam Long articleId) {
        return favoriteService.isFavorited(articleId);
    }
}