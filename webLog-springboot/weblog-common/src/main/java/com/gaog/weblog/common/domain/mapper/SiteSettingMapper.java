package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;

/**
 * 站点设置 Mapper
 */
public interface SiteSettingMapper extends BaseMapper<BlogSettingDO> {

    /**
     * 站点配置为单行，优先按 id=1 获取，不存在则取第一行
     */
    default BlogSettingDO findSingleton() {
        BlogSettingDO byId = selectById(1L);
        if (byId != null) {
            return byId;
        }
        LambdaQueryWrapper<BlogSettingDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }
}
