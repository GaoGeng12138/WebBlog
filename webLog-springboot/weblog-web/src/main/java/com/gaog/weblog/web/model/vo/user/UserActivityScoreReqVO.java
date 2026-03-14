package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityScoreReqVO {

    
    /**
     * Time range: daily, weekly, monthly, yearly, all
     */
    @NotNull(message = "Time range cannot be null")
    private String timeRange;
}