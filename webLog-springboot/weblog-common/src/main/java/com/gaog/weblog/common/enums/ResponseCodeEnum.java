package com.gaog.weblog.common.enums;

import com.gaog.weblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 15:26
 * @Version: 1.0
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {
    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    USERNAME_NOT_FOUND("20003", "该用户不存在"),
    PASSWORD_ERROR("20004", "旧密码错误，请重新输入！"),
    PASSWORD_NOT_SAME("20005", "两次密码输入不一致，请检查！"),
    NEW_NOT_SAME_FOR_OLD("20006", "新密码不能和旧密码一样，请重新输入！"),
    CATEGORY_NAME_IS_EXISTED("20007", "该分类已存在，请勿重复添加！"),
    TAG_NAME_IS_EXISTED("20008", "该标签已存在，请勿重复添加！"),
    CATEGORY_NOT_EXISTED("20009", "提交的分类不存在！"),
    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),
    FILE_UPLOAD_FAILED("20011", "文件上传失败！"),
    ARTICLE_NOT_EXISTED("20012", "该文章不存在！"),
    USERNAME_ALREADY_EXISTS("20013", "用户名已存在，请更换！"),
    REGISTRATION_FAILED("20014", "注册失败，请稍后重试！"),
    ACCOUNT_NOT_ENABLED("20015", "账户未启用，请联系管理员审核！"),
    USER_NOT_FOUND("20016", "用户不存在！"),
    UPDATE_USER_STATUS_FAILED("20017", "更新用户状态失败！"),
    CREATE_USER_FAILED("20018", "创建用户失败！"),
    DELETE_USER_FAILED("20019", "删除用户失败！"),
    UPDATE_USER_INFO_FAILED("20020", "更新用户信息失败！"),
    CANNOT_DELETE_CURRENT_USER("20021", "不能删除当前登录用户！"),
    USER_NOT_ENABLED("20022", "当前用户尚未启用，请联系管理员!"),
    COMMENT_DISABLED("20023", "评论功能已关闭！"),
    ANONYMOUS_COMMENT_DISABLED("20024", "未启用匿名评论，请先登录！"),
    COMMENT_REQUIRE_INFO("20025", "匿名评论需要提供昵称和邮箱！"),
    COMMENT_NOT_FOUND("20026", "该评论不存在！"),
    PERMISSION_DENIED("20027", "无权限执行此操作！"),
    ROLE_NAME_ALREADY_EXISTS("20028", "角色名称已存在，请更换！"),
    ROLE_NOT_FOUND("20029", "角色不存在！"),
    ROLE_NOT_ENABLED("20030", "角色未启用！"),
    ASSIGN_ROLE_FAILED("20031", "分配角色失败！"),
    UPDATE_ROLE_FAILED("20032", "更新角色失败！"),
    DELETE_ROLE_FAILED("20033", "删除角色失败！"),
    CREATE_ROLE_FAILED("20034", "创建角色失败！"),

    // ----------- 业务异常状态码 -----------
    PRODUCT_NOT_FOUND("20000", "该产品不存在（测试使用）"),
    FORBIDDEN("20004", "演示账号仅支持查询操作！"),


    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;
}
