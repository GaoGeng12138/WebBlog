package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityScoreRspVO {
    
    /**
     * Total activity score
     */
    private Integer totalScore;
    
    /**
     * User rank
     */
    private Integer rank;
    
    /**
     * Total number of users
     */
    private Integer totalUsers;
    
    /**
     * Activity breakdown
     */
    private List<ActivityDetailVO> activities;
    
    /**
     * Score trend over time
     */
    private List<ScoreTrendVO> trend;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActivityDetailVO {
        /**
         * Activity type: article, comment, favorite, login
         */
        private String type;

        /**
         * Activity display name
         */
        private String name;
        
        /**
         * Count of activities
         */
        private Integer count;
        
        /**
         * Score for this activity type
         */
        private Integer score;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ScoreTrendVO {
        /**
         * Date
         */
        private String date;
        
        /**
         * Score for this date
         */
        private Integer score;
    }
}
