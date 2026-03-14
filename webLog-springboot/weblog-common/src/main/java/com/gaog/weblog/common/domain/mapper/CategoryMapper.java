package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.CategoryDO;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/22 22:04
 * @Version: 1.0
 * @Description:
 */
public interface CategoryMapper extends BaseMapper<CategoryDO> {
    /**
     * 根据名称查询
     *
     * @param categoryName
     * @return
     */
    default CategoryDO selectByName(String categoryName) {
        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryDO::getName, categoryName);

        // 执行查询
        return selectOne(wrapper);
    }
}
