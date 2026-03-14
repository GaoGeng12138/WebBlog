package com.gaog.weblog.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSJ
 * @date 2025/11/24 17:24
 * @description 查询Select列表入参VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询Select列表出参 VO")
public class FindTagSelectListReqVO {
    /**
     * 标签名称
     */
    private String name;
}
