package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ZSJ
 * @date 2025/11/25 17:30
 * @description 文件服务接口
 */
public interface FileService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);
}
