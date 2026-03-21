package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_role_permission")
public class RolePermissionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private String permissionKey;

    private LocalDateTime createTime;
}
