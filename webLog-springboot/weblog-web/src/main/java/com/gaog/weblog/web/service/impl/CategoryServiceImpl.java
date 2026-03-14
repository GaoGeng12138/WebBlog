package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListRspVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryArticleReqVO;
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

    /**
     * 获取分类分页数据，包含每个分类的文章数量
     *
     * @param findCategoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findCategoryPageListReqVO.getCurrent();
        Long size = findCategoryPageListReqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<CategoryDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CategoryDO::getCreateTime);

        // 执行分页查询
        Page<CategoryDO> categoryDOPage = categoryMapper.selectPage(page, wrapper);

        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        // DO 转 VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 获取所有分类的ID
            List<Long> categoryIds = categoryDOS.stream()
                    .map(CategoryDO::getId)
                    .collect(Collectors.toList());

            // 查询每个分类下的文章数量
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
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
        
        // DO 转 VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 获取所有分类的ID
            List<Long> categoryIds = categoryDOS.stream()
                    .map(CategoryDO::getId)
                    .collect(Collectors.toList());

            // 查询每个分类下的文章数量
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
        // Simply return an empty response as this should be handled by ArticleService
        // In a real implementation, this would delegate to ArticleService
        Page page = new Page(findCategoryArticleReqVO.getCurrent(), findCategoryArticleReqVO.getSize());
        page.setTotal(0);
        return PageResponse.success(page, null);
    }
}