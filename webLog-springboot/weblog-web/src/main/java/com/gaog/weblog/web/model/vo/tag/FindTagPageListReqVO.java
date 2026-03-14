package com.gaog.weblog.web.model.vo.tag;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaog
 * @description 标签分页查询请求VO
 */
@Data
@Builder
@ApiModel(value = "标签分页查询请求VO")
public class FindTagPageListReqVO extends BasePageQuery {
}