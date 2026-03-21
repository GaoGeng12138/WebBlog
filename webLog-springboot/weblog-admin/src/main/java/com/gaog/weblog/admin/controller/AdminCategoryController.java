package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryShowOnFrontReqVO;
import com.gaog.weblog.admin.service.AdminCategoryService;
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
 *
 * @Author: gaoge
 * @Date: 2025/11/23 12:33
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 分类模块")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService categoryService;

    @PostMapping("/category/add")
    @ApiOperation(value = "添加分类")
    @ApiOperationLog(description = "添加分类")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:category:add')")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }


    @PostMapping("/category/list")
    @ApiOperation(value = "分类分页数据获取")
    @ApiOperationLog(description = "分类分页数据获取")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:category:list')")
    public PageResponse findCategoryList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryList(findCategoryPageListReqVO);
    }


    @PostMapping("/category/delete")
    @ApiOperation(value = "删除分类")
    @ApiOperationLog(description = "删除分类")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:category:delete')")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return categoryService.deleteCategory(deleteCategoryReqVO);
    }

    @PostMapping("/category/select/list")
    @ApiOperation(value = "分类 Select 下拉列表数据获取")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:category:list')")
    public Response findCategorySelectList() {
        return categoryService.findCategorySelectList();
    }

    @PostMapping("/category/update/showOnFront")
    @ApiOperation(value = "更新分类前台展示状态")
    @ApiOperationLog(description = "更新分类前台展示状态")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:category:update-front')")
    public Response updateCategoryShowOnFront(@RequestBody @Validated UpdateCategoryShowOnFrontReqVO updateCategoryShowOnFrontReqVO) {
        return categoryService.updateCategoryShowOnFront(updateCategoryShowOnFrontReqVO);
    }
}
