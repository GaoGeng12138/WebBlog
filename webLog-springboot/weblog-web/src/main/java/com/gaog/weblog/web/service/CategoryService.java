 package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.web.model.vo.category.FindCategoryArticleReqVO;

/**
 * @author gaog
 * @description 分类服务接口
 */
public interface CategoryService {
    /**
     * 获取分类分页数据
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO);
    
    /**
     * 获取所有分类数据
     * @return
     */
    Response findAllCategories();
    
    /**
     * 根据分类ID获取文章分页数据
     * @param findCategoryArticleReqVO
     * @return
     */
    PageResponse findArticlePageListByCategoryId(FindCategoryArticleReqVO findCategoryArticleReqVO);
}