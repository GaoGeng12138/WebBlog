package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.gaog.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryShowOnFrontReqVO;
import com.gaog.weblog.admin.model.vo.category.UpdateCategoryVisibilityReqVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/22 22:12
 * @Version: 1.0
 * @Description:
 */
public interface AdminCategoryService {
    /**
     * 添加分类
     *
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据查询
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO);


    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的 Select 列表数据
     * @return
     */
    Response findCategorySelectList();

    /**
     * 更新分类前台展示状态
     * @param updateCategoryShowOnFrontReqVO
     * @return
     */
    Response updateCategoryShowOnFront(UpdateCategoryShowOnFrontReqVO updateCategoryShowOnFrontReqVO);

    /**
     * 更新分类可见范围
     * @param updateCategoryVisibilityReqVO
     * @return
     */
    Response updateCategoryVisibility(UpdateCategoryVisibilityReqVO updateCategoryVisibilityReqVO);
}
