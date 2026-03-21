package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.service.ContentVisibilityService;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.category.FindCategoryArticleReqVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListRspVO;
import com.gaog.weblog.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaog
 * @description 分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ContentVisibilityService contentVisibilityService;

    /**
     * 获取分类分页数据，包含每个分类的文章数量
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
        wrapper.orderByDesc(CategoryDO::getCreateTime);
        applyVisibilityFilter(wrapper);

        Page<CategoryDO> categoryDOPage = categoryMapper.selectPage(page, wrapper);
        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            List<Long> categoryIds = categoryDOS.stream()
                    .map(CategoryDO::getId)
                    .collect(Collectors.toList());

            Map<Long, Integer> categoryArticleCountMap = articleCategoryRelMapper.selectList(
                            new LambdaQueryWrapper<ArticleCategoryRelDO>()
                                    .in(ArticleCategoryRelDO::getCategoryId, categoryIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            ArticleCategoryRelDO::getCategoryId,
                            Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                    ));

            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .illustrate(categoryDO.getIllustrate())
                            .articleCount(categoryArticleCountMap.getOrDefault(categoryDO.getId(), 0))
                            .showOnFront(!Boolean.FALSE.equals(categoryDO.getShowOnFront()))
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * 获取所有分类数据，包含每个分类的文章数量
     *
     * @return
     */
    @Override
    public Response findAllCategories() {
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            categoryDOS = categoryDOS.stream()
                    .filter(contentVisibilityService::canAccessCategory)
                    .collect(Collectors.toList());
        }

        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            List<Long> categoryIds = categoryDOS.stream()
                    .map(CategoryDO::getId)
                    .collect(Collectors.toList());

            Map<Long, Integer> categoryArticleCountMap = articleCategoryRelMapper.selectList(
                            new LambdaQueryWrapper<ArticleCategoryRelDO>()
                                    .in(ArticleCategoryRelDO::getCategoryId, categoryIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            ArticleCategoryRelDO::getCategoryId,
                            Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                    ));

            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .illustrate(categoryDO.getIllustrate())
                            .articleCount(categoryArticleCountMap.getOrDefault(categoryDO.getId(), 0))
                            .showOnFront(!Boolean.FALSE.equals(categoryDO.getShowOnFront()))
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 根据分类ID获取文章分页数据
     *
     * @param findCategoryArticleReqVO
     * @return
     */
    @Override
    public PageResponse findArticlePageListByCategoryId(FindCategoryArticleReqVO findCategoryArticleReqVO) {
        Page page = new Page(findCategoryArticleReqVO.getCurrent(), findCategoryArticleReqVO.getSize());
        page.setTotal(0);
        return PageResponse.success(page, null);
    }

    private void applyVisibilityFilter(LambdaQueryWrapper<CategoryDO> wrapper) {
        if (contentVisibilityService.isCurrentUserPrivileged()) {
            return;
        }

        Long currentUserId = contentVisibilityService.getCurrentUserIdSafely();
        if (currentUserId == null) {
            wrapper.eq(CategoryDO::getVisibilityScope, 1);
            return;
        }

        wrapper.and(w -> w.eq(CategoryDO::getVisibilityScope, 1)
                .or()
                .inSql(CategoryDO::getId, "SELECT category_id FROM t_category_access_user WHERE user_id = " + currentUserId));
    }
}
