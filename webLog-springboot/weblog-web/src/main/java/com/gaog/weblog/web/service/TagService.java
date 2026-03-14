package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.web.model.vo.tag.FindTagArticleReqVO;

/**
 * @author gaog
 * @description 标签服务接口
 */
public interface TagService {
    /**
     * 获取标签分页数据
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);
    
    /**
     * 获取所有标签数据
     * @return
     */
    Response findAllTags();
    
    /**
     * 根据标签ID获取文章分页数据
     * @param findTagArticleReqVO
     * @return
     */
    PageResponse findArticlePageListByTagId(FindTagArticleReqVO findTagArticleReqVO);
}