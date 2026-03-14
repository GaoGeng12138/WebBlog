package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author ZSJ
 * @date 2025/12/20
 * @description 统计表 - 文章 PV (访问量)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_statistics_article_pv")
public class StatisticsArticlePvDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 被统计的日期
     */
    private LocalDate pvDate;
    
    /**
     * pv访问量
     */
    private Long pvCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 最后一次更新时间
     */
    private LocalDateTime updateTime;
}
