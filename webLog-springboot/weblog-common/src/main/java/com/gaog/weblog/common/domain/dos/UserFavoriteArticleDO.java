package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName：UserFavoriteArticleDO
 * version:1.0.0
 *
 * @author: GaoG
 * Date: 2025/12/14
 * Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_favorite_article")
public class UserFavoriteArticleDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long articleId;

    private LocalDateTime createTime;
}