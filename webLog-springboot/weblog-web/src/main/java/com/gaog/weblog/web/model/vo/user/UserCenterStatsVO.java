package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCenterStatsVO {
    /**
     * 发布的文章数量
     */
    private Long articleCount;

    /**
     * 收藏的文章数量
     */
    private Long favoriteCount;

    /**
     * 发表的评论数量
     */
    private Long commentCount;
}