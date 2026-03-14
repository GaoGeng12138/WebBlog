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
 * @description 访客记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_visitor_log")
public class VisitorLogDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（登录用户）
     */
    private Long userId;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 访问日期
     */
    private LocalDate visitDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
