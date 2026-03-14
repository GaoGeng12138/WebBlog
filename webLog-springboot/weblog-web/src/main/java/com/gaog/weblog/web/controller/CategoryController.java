package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryArticleReqVO;
import com.gaog.weblog.web.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaog
 * @description 分类控制器
 */
@RestController
@Api(tags = "分类模块")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/category/list")
    @ApiOperation(value = "获取分类分页数据")
    @ApiOperationLog(description = "获取分类分页数据")
    public PageResponse findCategoryList(@RequestBody FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryList(findCategoryPageListReqVO);
    }
    
    @PostMapping("/category/list/all")
    @ApiOperation(value = "获取所有分类数据")
    @ApiOperationLog(description = "获取所有分类数据")
    public Response findAllCategories() {
        return categoryService.findAllCategories();
    }
    
    @PostMapping("/category/article/list")
    @ApiOperation(value = "根据分类ID获取文章分页数据")
    @ApiOperationLog(description = "根据分类ID获取文章分页数据")
    public PageResponse findArticlePageListByCategoryId(@RequestBody FindCategoryArticleReqVO findCategoryArticleReqVO) {
        return categoryService.findArticlePageListByCategoryId(findCategoryArticleReqVO);
    }
}