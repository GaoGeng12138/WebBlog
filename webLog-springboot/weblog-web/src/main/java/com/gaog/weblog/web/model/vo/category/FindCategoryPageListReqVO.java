package com.gaog.weblog.web.model.vo.category;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaog
 * @description 分类分页查询请求VO
 */
@Data
@Builder
@ApiModel(value = "分类分页查询请求VO")
public class FindCategoryPageListReqVO extends BasePageQuery {
}