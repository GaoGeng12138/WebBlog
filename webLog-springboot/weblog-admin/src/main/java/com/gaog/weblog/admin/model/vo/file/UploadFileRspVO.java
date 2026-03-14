package com.gaog.weblog.admin.model.vo.file;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSJ
 * @date 2025/11/25 17:30
 * @description 文件上传响应 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "文件上传响应 VO")
public class UploadFileRspVO {
    /**
     * 文件访问链接
     */
    private String url;
}
