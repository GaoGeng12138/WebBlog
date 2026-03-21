package com.gaog.weblog.admin.model.vo.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTreeNodeRspVO {

    private Long id;

    private String name;

    private String type;

    private String permissionKey;

    private List<PermissionTreeNodeRspVO> children;
}
