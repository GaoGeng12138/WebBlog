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
 * User Activity Score Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_activity_score")
public class UserActivityScoreDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * User ID
     */
    private Long userId;
    
    /**
     * Activity date
     */
    private LocalDate activityDate;
    
    /**
     * Total score for the day
     */
    private Integer dailyScore;
    
    /**
     * Article count for the day
     */
    private Integer articleCount;
    
    /**
     * Comment count for the day
     */
    private Integer commentCount;
    
    /**
     * Favorite count for the day
     */
    private Integer favoriteCount;
    
    /**
     * Login count for the day (should typically be 0 or 1)
     */
    private Integer loginCount;
    
    /**
     * Creation time
     */
    private LocalDateTime createTime;
    
    /**
     * Last update time
     */
    private LocalDateTime updateTime;
}