package com.gaog.weblog.admin.service;

import com.gaog.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ZSJ
 * @date 2025/11/25 17:30
 * @description 文件服务接口
 */
public interface AdminFileService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);

    /**
     * 解析 Word 文档为 Markdown。
     *
     * @param file Word 文件
     * @return 解析结果
     */
    Response parseWordFile(MultipartFile file);
}
