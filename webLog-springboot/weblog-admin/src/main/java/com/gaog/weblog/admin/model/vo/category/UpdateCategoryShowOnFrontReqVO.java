package com.gaog.weblog.admin.model.vo.category;

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
@ApiModel(value = "更新分类前台展示状态 VO")
public class UpdateCategoryShowOnFrontReqVO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;

    @NotNull(message = "前台展示状态不能为空")
    private Boolean showOnFront;
}
