package com.gaog.weblog.admin.model.vo.category;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 更新分类可见性请求对象。
 */
@ApiModel(value = "更新分类可见性 VO")
public class UpdateCategoryVisibilityReqVO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;

    private Integer visibilityScope;

    private List<Long> visibleUserIds;
}
