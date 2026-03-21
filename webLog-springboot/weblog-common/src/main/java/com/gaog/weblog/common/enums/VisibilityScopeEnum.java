package com.gaog.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisibilityScopeEnum {

    PUBLIC(1, "公开"),
    ASSIGNED_USERS(2, "指定用户可见");

    private final Integer code;
    private final String desc;

    public static Integer normalize(Integer code) {
        return ASSIGNED_USERS.code.equals(code) ? ASSIGNED_USERS.code : PUBLIC.code;
    }
}
