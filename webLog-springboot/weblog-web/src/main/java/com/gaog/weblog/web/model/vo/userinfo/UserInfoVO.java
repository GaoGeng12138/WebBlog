package com.gaog.weblog.web.model.vo.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Gao
 * @description 用户信息返回 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     *
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个性签名
     */
    private String introduction;

    /**
     * 社交链接
     */
    private String githubUrl;
    private String twitterUrl;
    private String weiboUrl;


    /**
     * 角色集合
     */
    private Set<String> roles;

    /**
     * 注册时间
     */
    private Date registerTime;
}