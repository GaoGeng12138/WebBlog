package com.gaog.weblog.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/23 12:29
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询分类分页数据出参 VO")
public class FindTagPageListRspVO {
    /**
     * tag ID
     */
    private Long id;

    /**
     * tag name
     */
    private String name;

    /**
     * createTime
     */
    private LocalDateTime createTime;

}