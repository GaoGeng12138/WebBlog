package com.gaog.weblog.web.model.vo.category;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "根据分类ID获取文章分页数据入参 VO")
public class FindCategoryArticleReqVO {

    /**
     * 当前页码
     */
    @NotNull(message = "当前页码不能为空")
    private Long current;

    /**
     * 每页展示的数量
     */
    @NotNull(message = "每页展示的数量不能为空")
    private Long size;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
}