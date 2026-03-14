package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.RoleDO;

/**
 * Role Mapper for database operations
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 * @Description: MyBatis Plus Mapper for role management
 */
public interface RoleMapper extends BaseMapper<RoleDO> {

    /**
     * Query role by name
     *
     * @param roleName role name
     * @return RoleDO
     */
    default RoleDO selectByName(String roleName) {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getName, roleName);
        return selectOne(wrapper);
    }

    /**
     * Query role by ID, excluding deleted roles
     *
     * @param id role ID
     * @return RoleDO
     */
    default RoleDO selectByIdNotDeleted(Long id) {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getId, id)
               .eq(RoleDO::getIsDeleted, false);
        return selectOne(wrapper);
    }
}
