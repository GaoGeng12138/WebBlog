package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityRankingRspVO {
    
    /**
     * User ID
     */
    private Long userId;
    
    /**
     * Username
     */
    private String username;
    
    /**
     * User avatar
     */
    private String avatar;
    
    /**
     * Total score
     */
    private Integer score;
    
    /**
     * User rank
     */
    private Integer rank;
}