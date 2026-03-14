package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gaog.weblog.common.domain.dos.StatisticsArticlePvDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * @author ZSJ
 * @date 2025/12/20
 * @description 统计表 - 文章 PV Mapper
 */
public interface StatisticsArticlePvMapper extends BaseMapper<StatisticsArticlePvDO> {
    
    /**
     * 增加PV访问量（如果当天记录不存在则创建）
     * 
     * @param date 日期
     * @return 影响行数
     */
    default int increasePvCount(LocalDate date) {
        // 查询当天的记录
        LambdaQueryWrapper<StatisticsArticlePvDO> wrapper = Wrappers.<StatisticsArticlePvDO>lambdaQuery()
                .eq(StatisticsArticlePvDO::getPvDate, date);
        StatisticsArticlePvDO pvDO = selectOne(wrapper);
        
        if (pvDO != null) {
            // 记录存在，增加计数
            pvDO.setPvCount(pvDO.getPvCount() + 1);
            return updateById(pvDO);
        } else {
            // 记录不存在，创建新记录
            StatisticsArticlePvDO newPvDO = StatisticsArticlePvDO.builder()
                    .pvDate(date)
                    .pvCount(1L)
                    .build();
            return insert(newPvDO);
        }
    }
}
