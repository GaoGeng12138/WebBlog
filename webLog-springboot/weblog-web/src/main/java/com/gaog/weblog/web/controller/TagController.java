package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.tag.FindTagArticleReqVO;
import com.gaog.weblog.web.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.web.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaog
 * @description 标签控制器
 */
@RestController
@Api(tags = "标签模块")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/tag/list")
    @ApiOperation(value = "获取标签分页数据")
    @ApiOperationLog(description = "获取标签分页数据")
    public PageResponse findTagList(@RequestBody FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagList(findTagPageListReqVO);
    }

    @PostMapping("/tag/list/all")
    @ApiOperation(value = "获取所有标签数据")
    @ApiOperationLog(description = "获取所有标签数据")
    public Response findAllTags() {
        return tagService.findAllTags();
    }

    @PostMapping("/tag/article/list")
    @ApiOperation(value = "根据标签ID获取文章分页数据")
    @ApiOperationLog(description = "根据标签ID获取文章分页数据")
    public PageResponse findArticlePageListByTagId(@RequestBody FindTagArticleReqVO findTagArticleReqVO) {
        return tagService.findArticlePageListByTagId(findTagArticleReqVO);
    }
}