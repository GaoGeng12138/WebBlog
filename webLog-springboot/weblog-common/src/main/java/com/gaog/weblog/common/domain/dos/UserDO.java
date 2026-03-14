package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 21:04
 * @Version: 1.0
 * @Description: 根据阿里的开发规范，统一将数据库对应的实体类命名为 xxxDO 这种形式，统一存放此包下。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user")
public class UserDO {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String avatar;


    /**
     * 昵称
     */
    private String nickname;


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
     * 账户启用状态
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

    private Date createTime;

    private Date updateTime;

    @TableField("is_deleted")
    private Boolean isDeleted;
}
