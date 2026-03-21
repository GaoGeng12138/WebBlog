package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.RolePermissionDO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface RolePermissionMapper extends BaseMapper<RolePermissionDO> {

    default List<RolePermissionDO> selectByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        return selectList(wrapper);
    }

    default int deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return 0;
        }

        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        return delete(wrapper);
    }

    default List<String> selectPermissionKeysByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RolePermissionDO::getRoleId, roleIds);

        List<RolePermissionDO> rolePermissions = selectList(wrapper);
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return Collections.emptyList();
        }

        return rolePermissions.stream()
                .map(RolePermissionDO::getPermissionKey)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
