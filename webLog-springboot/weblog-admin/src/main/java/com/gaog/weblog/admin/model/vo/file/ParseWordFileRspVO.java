package com.gaog.weblog.admin.model.vo.file;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Word 文档解析响应 VO。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Word 文档解析响应 VO")
public class ParseWordFileRspVO {

    /**
     * 建议的文章标题。
     */
    private String title;

    /**
     * 解析后的 Markdown 正文。
     */
    private String content;
}
