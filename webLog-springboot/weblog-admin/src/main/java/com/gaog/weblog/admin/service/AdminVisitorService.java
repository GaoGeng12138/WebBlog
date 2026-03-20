package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.visitor.FindVisitorLogPageListReqVO;
import com.gaog.weblog.common.utils.PageResponse;

public interface AdminVisitorService {

    /**
     * 查询访客记录分页列表
     *
     * @param reqVO 查询条件
     * @return 分页数据
     */
    PageResponse findVisitorLogPageList(FindVisitorLogPageListReqVO reqVO);
}
