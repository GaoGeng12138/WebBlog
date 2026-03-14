package com.gaog.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/13 18:45
 * @Version: 1.0
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum ArticleStatusEnum {

    PUBLISH(4, "发布"),
    NON_PUBLISH(3, "未发布"),
    NON_Approved(2, "审核未通过"),
    APPROVED(1, "审核通过"),
    TO_DO_APPROVE(0, "待审核"),
    ;
    private final Integer code;
    private final String status;
}
