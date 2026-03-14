package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.tag.FindTagSelectListReqVO;
import com.gaog.weblog.admin.model.vo.tag.AddTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;

/**
 * @author ZSJ
 * @date 2025/11/24 17:00
 * @description 标签业务层
 */
public interface AdminTagService {


    /**
     * 添加标签
     *
     * @param addTagReqVO
     * @return
     */
    Response addTag(AddTagReqVO addTagReqVO);

    /**
     * 分类分页数据查询
     *
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);


    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 获取标签分类的 Select 列表数据
     *
     * @return
     */
    Response findTagSelectList(FindTagSelectListReqVO findTagSelectListReqVO);
}
