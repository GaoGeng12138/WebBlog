package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListRspVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryShowOnFrontReqVO;
import com.gaog.weblog.admin.service.AdminCategoryService;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.model.vo.SelectRspVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/22 22:12
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class AdminCategoryServiceimpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     *
     * @param addCategoryReqVO
     * @return
     */
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String categoryName = addCategoryReqVO.getName();

        // 先判断该分类是否已经存在
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);

        if (Objects.nonNull(categoryDO)) {
            log.warn("分类名称： {}, 此分类已存在", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        // 构建 DO 类
        CategoryDO insertCategoryDO = CategoryDO.builder()
                .name(addCategoryReqVO.getName().trim())
                .illustrate(addCategoryReqVO.getIllustrate().trim())
                .showOnFront(Boolean.TRUE.equals(addCategoryReqVO.getShowOnFront()))
                .build();

        // 执行 insert
        categoryMapper.insert(insertCategoryDO);

        return Response.success();
    }

    /**
     * 分类分页数据查询
     *
     * @param findCategoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        Long current = findCategoryPageListReqVO.getCurrent();
        Long size = findCategoryPageListReqVO.getSize();

        Page<CategoryDO> page = new Page<>(current, size);
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();

        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();

        wrapper
                .like(StringUtils.isNotBlank(name), CategoryDO::getName, name.trim())
                .ge(Objects.nonNull(startDate), CategoryDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), CategoryDO::getCreateTime, endDate)
                .orderByDesc(CategoryDO::getCreateTime);

        Page<CategoryDO> categoryDOPage = categoryMapper.selectPage(page, wrapper);
        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .illustrate(categoryDO.getIllustrate())
                            .showOnFront(!Boolean.FALSE.equals(categoryDO.getShowOnFront()))
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * 删除分类
     *
     * @param deleteCategoryReqVO
     * @return
     */
    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        Long categoryId = deleteCategoryReqVO.getId();
        categoryMapper.deleteById(categoryId);
        return Response.success();
    }

    /**
     * 获取文章分类的 Select 列表数据
     *
     * @return
     */
    @Override
    public Response findCategorySelectList() {
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        List<SelectRspVO> selectRspVOS = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }

    @Override
    public Response updateCategoryShowOnFront(UpdateCategoryShowOnFrontReqVO updateCategoryShowOnFrontReqVO) {
        Long categoryId = updateCategoryShowOnFrontReqVO.getId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        CategoryDO updateCategoryDO = CategoryDO.builder()
                .id(categoryId)
                .showOnFront(Boolean.TRUE.equals(updateCategoryShowOnFrontReqVO.getShowOnFront()))
                .build();
        categoryMapper.updateById(updateCategoryDO);
        return Response.success();
    }
}
