package com.gaog.weblog.web.model.vo.article;

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
@ApiModel(value = "根据标签ID获取文章分页数据入参 VO")
public class FindArticleByTagReqVO {

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
     * 关键字
     */
    private String name;
    
    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;
}
