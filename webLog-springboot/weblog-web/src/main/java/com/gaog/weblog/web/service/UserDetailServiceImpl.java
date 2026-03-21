package com.gaog.weblog.web.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.gaog.weblog.common.domain.dos.RoleDO;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.UserRoleDO;
import com.gaog.weblog.common.domain.mapper.RoleMapper;
import com.gaog.weblog.common.domain.mapper.RolePermissionMapper;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.UserRoleMapper;
import com.gaog.weblog.jwt.exception.AccountDisabledException;
import com.gaog.weblog.jwt.model.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 22:34
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;


    /**
     * loadUserByUsername 方法是 Spring Security 框架中的核心方法之一
     * 用于根据用户名加载用户信息，实现用户认证功能
     *
     * @param username 用户名，用于从数据库或其他存储中查找用户信息
     * @return UserDetails 包含用户信息的对象，如用户名、密码、权限等
     * @throws UsernameNotFoundException 当用户名不存在时抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("用户名: {}", username);

        UserDO userDO = userMapper.findByUsername(username);

        //TODO  用户不存在 这个异常抛出有问题
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 检查用户是否已启用（兼容旧数据：null 视为已启用）
        if (userDO.getIsEnabled() != null && !userDO.getIsEnabled()) {
            throw new AccountDisabledException("账户未启用，请联系管理员审核");
        }

        //Get user roles
        List<UserRoleDO> roleDOS = userRoleMapper.selectByUserId(userDO.getId());

        List<String> roles = null;
        List<String> permissions = null;

        if (CollectionUtils.isNotEmpty(roleDOS)) {
            // Query role names by role IDs
            List<Long> roleIds = roleDOS.stream()
                    .map(UserRoleDO::getRoleId)
                    .collect(Collectors.toList());
            
            if (CollectionUtils.isNotEmpty(roleIds)) {
                List<RoleDO> roleLists = roleMapper.selectBatchIds(roleIds);
                if (CollectionUtils.isNotEmpty(roleLists)) {
                    roles = roleLists.stream()
                            .map(RoleDO::getName)
                            .collect(Collectors.toList());
                }

                permissions = rolePermissionMapper.selectPermissionKeysByRoleIds(roleIds);
            }
        }

        // 返回自定义的 CustomUserDetails，包含完整的用户信息
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .userId(userDO.getId())
                .username(userDO.getUsername())
                .password(userDO.getPassword())
                .avatar(userDO.getAvatar())
                .nickname(userDO.getNickname())
                .email(userDO.getEmail())
                .introduction(userDO.getIntroduction())
                .githubUrl(userDO.getGithubUrl())
                .twitterUrl(userDO.getTwitterUrl())
                .weiboUrl(userDO.getWeiboUrl())
                .roles(roles)
                .permissions(permissions)
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .createTime(userDO.getCreateTime())
                .build();
        return customUserDetails;
    }
}
