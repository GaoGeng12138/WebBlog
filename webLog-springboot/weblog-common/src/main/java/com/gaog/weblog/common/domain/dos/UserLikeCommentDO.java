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
 * User Like Comment Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_like_comment")
public class UserLikeCommentDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long commentId;

    private LocalDateTime createTime;
}
