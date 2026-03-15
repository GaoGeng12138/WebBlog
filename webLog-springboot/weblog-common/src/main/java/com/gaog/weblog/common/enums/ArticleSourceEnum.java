package com.gaog.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleSourceEnum {

    ADMIN(1, "后台发布"),
    FRONTEND(2, "前台发布");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }

        for (ArticleSourceEnum value : values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }

        return "";
    }
}
