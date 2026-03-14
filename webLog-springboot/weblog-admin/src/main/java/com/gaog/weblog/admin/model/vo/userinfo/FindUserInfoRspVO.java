package com.gaog.weblog.admin.model.vo.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/21 23:20
 * @Version: 1.0
 * @Description: 查询用户信息返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserInfoRspVO implements Serializable {
    private static final long serialVersionUID = -5934855591041376457L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 角色
     */
    private Set<String> roles;

}
