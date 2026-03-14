package com.gaog.weblog.web.service.impl;

import com.gaog.weblog.admin.model.vo.file.UploadFileRspVO;
import com.gaog.weblog.admin.utils.MinioUtil;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/13 20:11
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = minioUtil.uploadFile(file,"frontend");

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> frontend 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
