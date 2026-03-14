package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityRankingReqVO {
    
    /**
     * Page number (starting from 1)
     */
    @Min(value = 1, message = "Page must be greater than or equal to 1")
    @NotNull(message = "Page cannot be null")
    private Integer page;
    
    /**
     * Page size
     */
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @NotNull(message = "Size cannot be null")
    private Integer size;
    
    /**
     * Time range: daily, weekly, monthly, yearly, all
     */
    @NotNull(message = "Time range cannot be null")
    private String timeRange;
}