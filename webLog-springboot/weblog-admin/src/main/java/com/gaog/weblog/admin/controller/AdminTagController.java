package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.tag.FindTagSelectListReqVO;
import com.gaog.weblog.admin.model.vo.tag.AddTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.admin.service.AdminTagService;
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
 * @date 2025/11/25 14:42
 * @description tag标签接口管理层
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 标签模块")
public class AdminTagController {
    @Autowired
    private AdminTagService tagService;


    @PostMapping("/tag/add")
    @ApiOperation(value = "添加标签")
    @ApiOperationLog(description = "添加标签")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addTag(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return tagService.addTag(addTagReqVO);
    }


    @PostMapping("/tag/list")
    @ApiOperation(value = "标签分页数据获取")
    @ApiOperationLog(description = "标签分页数据获取")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PageResponse findTagList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagList(findTagPageListReqVO);
    }


    @PostMapping("/tag/delete")
    @ApiOperation(value = "删除标签")
    @ApiOperationLog(description = "删除标签")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteCategory(@RequestBody @Validated DeleteTagReqVO deleteCategoryReqVO) {
        return tagService.deleteTag(deleteCategoryReqVO);
    }

    @PostMapping("/tag/select/list")
    @ApiOperation(value = "标签 Select 下拉列表数据获取")
    @ApiOperationLog(description = "标签 Select 下拉列表数据获取")
    public Response findCategorySelectList(@RequestBody @Validated FindTagSelectListReqVO findTagSelectListReqVO) {
        return tagService.findTagSelectList(findTagSelectListReqVO);
    }
}
