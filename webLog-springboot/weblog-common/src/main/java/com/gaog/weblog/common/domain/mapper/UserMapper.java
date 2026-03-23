package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.UserDO;

import java.time.LocalDateTime;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 21:05
 * @Version: 1.0
 * @Description:
 */
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 通过名称查询用户
     *
     * @param username
     * @return
     */
    default UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username)
                .eq(UserDO::getIsDeleted, false);
        return selectOne(wrapper);
    }

    /**
     * 通过用户名更新密码
     *
     * @param username
     * @param password
     * @return
     */
    default int updatePasswordByUsername(String username, String password) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserDO::getPassword, password);
        wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());
        // 更新条件
        wrapper.eq(UserDO::getUsername, username);

        return update(null, wrapper);
    }

}
