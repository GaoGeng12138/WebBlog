package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/9 23:42
 * @Version: 1.0
 * @Description:
 */
public interface ArchiveService {

    /**
     * 获取文章归档分页数据
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO);
}
