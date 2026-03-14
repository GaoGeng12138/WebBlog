package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author Qwen
 * @date 2025/12/20
 * @description User activity trend request VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityTrendReqVO {

    /**
     * 时间范围类型: daily, weekly, monthly, yearly
     */
    @NotBlank(message = "时间范围类型不能为空")
    private String timeRange;

}