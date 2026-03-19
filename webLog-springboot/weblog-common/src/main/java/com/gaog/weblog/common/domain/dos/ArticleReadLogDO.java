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
 * @author gaoge
 * @date 2026/3/19
 * @description 文章阅读记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_article_read_log")
public class ArticleReadLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID（登录用户）
     */
    private Long userId;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 阅读日期
     */
    private LocalDate readDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
