package com.gaog.weblog.common.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ZSJ
 * @date 2025/11/25 16:23
 * @description
 */
public interface InsertBatchMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     */

    int insertBatchSomeColumn(@Param("list") List<T> list);
}
