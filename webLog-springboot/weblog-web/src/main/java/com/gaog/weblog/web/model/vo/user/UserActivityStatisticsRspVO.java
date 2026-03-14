package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityStatisticsRspVO {
    
    /**
     * Daily average score
     */
    private Integer dailyAverage;
    
    /**
     * Weekly average score
     */
    private Integer weeklyAverage;
    
    /**
     * Monthly average score
     */
    private Integer monthlyAverage;
    
    /**
     * Best day date
     */
    private String bestDay;
    
    /**
     * Best day score
     */
    private Integer bestDayScore;
}