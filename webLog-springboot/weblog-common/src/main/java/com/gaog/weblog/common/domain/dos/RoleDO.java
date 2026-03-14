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
 * Role entity for role management
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 * @Description: Role data object for managing system roles
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_role")
public class RoleDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Role name (e.g., ROLE_ADMIN, ROLE_EDITOR, ROLE_VISITOR)
     */
    private String name;

    /**
     * Role description
     */
    private String description;

    /**
     * Role status: 1 = enabled, 0 = disabled
     */
    private Boolean isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * Logical delete flag: 0 = not deleted, 1 = deleted
     */
    private Boolean isDeleted;
}
