package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSJ
 * @date 2025/12/2 10:40
 * @description Page view trend response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PvTrendRspVO {

    /**
     * 日期 (yyyy-MM-dd)
     */
    private String date;

    /**
     * 浏览量
     */
    private Long pv;

}