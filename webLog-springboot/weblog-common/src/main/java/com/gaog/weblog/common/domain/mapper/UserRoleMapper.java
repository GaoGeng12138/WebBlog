package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.UserRoleDO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/20 23:22
 * @Version: 1.0
 * @Description:
 */
public interface UserRoleMapper extends BaseMapper<UserRoleDO> {
    /**
     * Query roles by userId
     *
     * @param userId userId
     * @return list of UserRoleDO
     */
    default List<UserRoleDO> selectByUserId(Long userId) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUserId, userId);
        return selectList(wrapper);
    }

    /**
     * Query role IDs by userId
     *
     * @param userId userId
     * @return set of role IDs
     */
    default Set<Long> selectRoleIdsByUserId(Long userId) {
        List<UserRoleDO> userRoleDOS = selectByUserId(userId);
        return userRoleDOS.stream().map(UserRoleDO::getRoleId).collect(Collectors.toSet());
    }

    /**
     * Delete all roles for a user by userId
     *
     * @param userId userId
     * @return affected rows
     */
    default int deleteByUserId(Long userId) {
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUserId, userId);
        return delete(wrapper);
    }

    /**
     * Query roles by username (deprecated, kept for compatibility)
     *
     * @param username username
     * @return list of UserRoleDO
     * @deprecated Use selectByUserId instead
     */
    @Deprecated
    default List<UserRoleDO> selectByUsername(String username) {
        // This method is deprecated. Use selectByUserId instead.
        throw new UnsupportedOperationException("selectByUsername is deprecated. Use selectByUserId instead.");
    }

    /**
     * Query role by username (deprecated, kept for compatibility)
     *
     * @param username username
     * @return set of role strings
     * @deprecated Use selectRoleIdsByUserId instead
     */
    @Deprecated
    default Set<String> selectRolesByUsername(String username) {
        // This method is deprecated. Use selectRoleIdsByUserId instead.
        throw new UnsupportedOperationException("selectRolesByUsername is deprecated. Use selectRoleIdsByUserId instead.");
    }

    /**
     * Delete by username (deprecated, kept for compatibility)
     *
     * @param username username
     * @return affected rows
     * @deprecated Use deleteByUserId instead
     */
    @Deprecated
    default int deleteByUsername(String username) {
        // This method is deprecated. Use deleteByUserId instead.
        throw new UnsupportedOperationException("deleteByUsername is deprecated. Use deleteByUserId instead.");
    }
}
