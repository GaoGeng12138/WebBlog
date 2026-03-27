package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListRspVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryShowOnFrontReqVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryVisibilityReqVO;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.CategoryAccessUserDO;
import com.gaog.weblog.admin.service.AdminCategoryService;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.CategoryAccessUserMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.enums.VisibilityScopeEnum;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.model.vo.SelectRspVO;
import com.gaog.weblog.common.service.ContentVisibilityService;
import com.gaog.weblog.common.utils.CategoryTreeUtil;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private CategoryAccessUserMapper categoryAccessUserMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ContentVisibilityService contentVisibilityService;

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

        Integer visibilityScope = contentVisibilityService.canCurrentUserConfigureVisibility()
                ? contentVisibilityService.normalizeScope(addCategoryReqVO.getVisibilityScope())
                : VisibilityScopeEnum.PUBLIC.getCode();

        Long parentId = addCategoryReqVO.getParentId();
        if (Objects.nonNull(parentId)) {
            CategoryDO parentCategory = categoryMapper.selectById(parentId);
            if (Objects.isNull(parentCategory)) {
                throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
            }
        }

        // 构建 DO 类
        CategoryDO insertCategoryDO = CategoryDO.builder()
                .name(addCategoryReqVO.getName().trim())
                .illustrate(addCategoryReqVO.getIllustrate().trim())
                .parentId(parentId)
                .showOnFront(Boolean.TRUE.equals(addCategoryReqVO.getShowOnFront()))
                .visibilityScope(visibilityScope)
                .build();

        // 执行 insert
        categoryMapper.insert(insertCategoryDO);

        saveVisibleUsers(insertCategoryDO.getId(), visibilityScope, addCategoryReqVO.getVisibleUserIds());

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
        List<CategoryDO> allCategoryDOS = categoryMapper.selectList(null);
        Map<Long, CategoryDO> categoryMap = CollectionUtils.isEmpty(allCategoryDOS)
                ? new HashMap<>()
                : allCategoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, item -> item, (left, right) -> left, LinkedHashMap::new));

        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .illustrate(categoryDO.getIllustrate())
                            .parentId(categoryDO.getParentId())
                            .parentName(resolveParentName(categoryDO.getParentId(), categoryMap))
                            .showOnFront(!Boolean.FALSE.equals(categoryDO.getShowOnFront()))
                            .visibilityScope(contentVisibilityService.normalizeScope(categoryDO.getVisibilityScope()))
                            .visibleUserIds(findVisibleUserIds(categoryDO.getId()))
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

        Long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<CategoryDO>()
                .eq(CategoryDO::getParentId, categoryId));
        if (Objects.nonNull(childCount) && childCount > 0) {
            throw new BizException(ResponseCodeEnum.CATEGORY_HAS_CHILDREN);
        }

        Long articleCount = articleCategoryRelMapper.selectCount(new LambdaQueryWrapper<ArticleCategoryRelDO>()
                .eq(ArticleCategoryRelDO::getCategoryId, categoryId));
        if (Objects.nonNull(articleCount) && articleCount > 0) {
            throw new BizException(ResponseCodeEnum.CATEGORY_HAS_ARTICLES);
        }

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
            Map<Long, CategoryDO> categoryMap = categoryDOS.stream()
                    .collect(Collectors.toMap(CategoryDO::getId, item -> item, (left, right) -> left, LinkedHashMap::new));
            Map<Long, List<CategoryDO>> childrenMap = categoryDOS.stream()
                    .filter(categoryDO -> Objects.nonNull(categoryDO.getParentId()))
                    .collect(Collectors.groupingBy(CategoryDO::getParentId, LinkedHashMap::new, Collectors.toList()));

            List<CategoryDO> rootCategories = categoryDOS.stream()
                    .filter(categoryDO -> Objects.isNull(categoryDO.getParentId()))
                    .sorted(Comparator.comparing(CategoryDO::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            selectRspVOS = new ArrayList<>();
            for (CategoryDO rootCategory : rootCategories) {
                buildCategorySelectOptions(rootCategory, "", categoryMap, childrenMap, selectRspVOS);
            }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateCategoryVisibility(UpdateCategoryVisibilityReqVO updateCategoryVisibilityReqVO) {
        Long categoryId = updateCategoryVisibilityReqVO.getId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        Integer visibilityScope = contentVisibilityService.canCurrentUserConfigureVisibility()
                ? contentVisibilityService.normalizeScope(updateCategoryVisibilityReqVO.getVisibilityScope())
                : VisibilityScopeEnum.PUBLIC.getCode();

        categoryMapper.updateById(CategoryDO.builder()
                .id(categoryId)
                .visibilityScope(visibilityScope)
                .updateTime(LocalDateTime.now())
                .build());

        saveVisibleUsers(categoryId, visibilityScope, updateCategoryVisibilityReqVO.getVisibleUserIds());
        return Response.success();
    }

    private void saveVisibleUsers(Long categoryId, Integer visibilityScope, List<Long> visibleUserIds) {
        categoryAccessUserMapper.delete(new LambdaQueryWrapper<CategoryAccessUserDO>()
                .eq(CategoryAccessUserDO::getCategoryId, categoryId));

        if (!VisibilityScopeEnum.ASSIGNED_USERS.getCode().equals(visibilityScope)) {
            return;
        }

        List<Long> normalizedUserIds = contentVisibilityService.normalizeAssignedUserIds(visibleUserIds);
        if (CollectionUtils.isEmpty(normalizedUserIds)) {
            return;
        }

        normalizedUserIds.forEach(userId -> categoryAccessUserMapper.insert(CategoryAccessUserDO.builder()
                .categoryId(categoryId)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .build()));
    }

    private List<Long> findVisibleUserIds(Long categoryId) {
        return categoryAccessUserMapper.selectList(new LambdaQueryWrapper<CategoryAccessUserDO>()
                        .eq(CategoryAccessUserDO::getCategoryId, categoryId))
                .stream()
                .map(CategoryAccessUserDO::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 构建分类下拉树。
     */
    private void buildCategorySelectOptions(CategoryDO categoryDO,
                                            String pathPrefix,
                                            Map<Long, CategoryDO> categoryMap,
                                            Map<Long, List<CategoryDO>> childrenMap,
                                            List<SelectRspVO> result) {
        if (Objects.isNull(categoryDO)) {
            return;
        }

        String currentLabel = StringUtils.isBlank(pathPrefix)
                ? categoryDO.getName()
                : pathPrefix + " / " + categoryDO.getName();
        result.add(SelectRspVO.builder()
                .label(currentLabel)
                .value(categoryDO.getId())
                .build());

        List<CategoryDO> children = childrenMap.get(categoryDO.getId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        List<CategoryDO> sortedChildren = children.stream()
                .sorted(Comparator.comparing(CategoryDO::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        for (CategoryDO childCategory : sortedChildren) {
            buildCategorySelectOptions(childCategory, currentLabel, categoryMap, childrenMap, result);
        }
    }

    /**
     * 获取父级分类名称。
     */
    private String resolveParentName(Long parentId, Map<Long, CategoryDO> categoryMap) {
        if (parentId == null || categoryMap == null || categoryMap.isEmpty()) {
            return null;
        }

        CategoryDO parentCategory = categoryMap.get(parentId);
        return parentCategory == null ? null : parentCategory.getName();
    }
}
