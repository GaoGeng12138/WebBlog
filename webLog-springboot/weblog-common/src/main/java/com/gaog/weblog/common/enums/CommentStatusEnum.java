package com.gaog.weblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Comment status enum
 */
@Getter
@AllArgsConstructor
public enum CommentStatusEnum {
    
    PENDING(0, "Pending Review"),
    APPROVED(1, "Approved"),
    REJECTED(2, "Rejected"),
    DELETED(3, "Deleted");

    private final Integer code;
    private final String description;
}
