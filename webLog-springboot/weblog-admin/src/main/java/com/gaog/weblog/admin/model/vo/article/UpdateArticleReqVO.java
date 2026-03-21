package com.gaog.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ZSJ
 * @date 2025/11/25 17:10
 * @description 更新文章 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新文章 VO")
public class UpdateArticleReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long id;

    @Length(max = 40, message = "文章标题字数需小于 40")
    private String title;

    private String content;

    private String cover;

    private String summary;

    private Long categoryId;

    private List<String> tags;

    private String submitAction;

    private Integer visibilityScope;

    private List<Long> visibleUserIds;
}
