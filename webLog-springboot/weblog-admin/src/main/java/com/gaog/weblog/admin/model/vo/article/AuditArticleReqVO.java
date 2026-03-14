package com.gaog.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/13 19:03
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "审核文章 VO")
public class AuditArticleReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private Long id;

    @NotNull(message = "文章状态不能为空")
    private Integer status;
}
