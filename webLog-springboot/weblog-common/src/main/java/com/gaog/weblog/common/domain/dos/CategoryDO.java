package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/22 22:03
 * @Version: 1.0
 * @Description: 分类表对应的实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_category")
public class CategoryDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String illustrate;

    /**
     * 是否在前台导航展示
     */
    private Boolean showOnFront;

    /**
     * 可见范围：1-公开，2-指定用户可见
     */
    private Integer visibilityScope;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;
}
