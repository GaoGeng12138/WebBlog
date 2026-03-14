package com.gaog.weblog.common.model;

import lombok.Data;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/23 12:26
 * @Version: 1.0
 * @Description:
 */
@Data
public class BasePageQuery {
    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;
}
