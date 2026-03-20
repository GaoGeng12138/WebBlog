package com.gaog.weblog.web.model.vo.userinfo;

import com.gaog.weblog.common.annotation.SensitiveField;
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
    @SensitiveField(request = false)
    private String username;

    /**
     * 昵称
     */
    @SensitiveField(request = false)
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    @SensitiveField(request = false)
    private String email;

    /**
     * 个性签名
     */
    @SensitiveField(request = false)
    private String introduction;

    /**
     * 社交链接
     */
    @SensitiveField(request = false)
    private String githubUrl;
    @SensitiveField(request = false)
    private String twitterUrl;
    @SensitiveField(request = false)
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
