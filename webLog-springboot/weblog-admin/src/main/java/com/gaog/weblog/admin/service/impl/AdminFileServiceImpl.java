package com.gaog.weblog.admin.service.impl;

import com.gaog.weblog.admin.model.vo.file.ParseWordFileRspVO;
import com.gaog.weblog.admin.model.vo.file.UploadFileRspVO;
import com.gaog.weblog.admin.service.AdminFileService;
import com.gaog.weblog.admin.utils.MinioUtil;
import com.gaog.weblog.admin.utils.WordMarkdownConverter;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author ZSJ
 * @date 2025/11/25 17:30
 * @description 文件服务实现
 */
@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

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
            String url = minioUtil.uploadFile(file,"admin");

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> admin 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 将上传的 Word 文件解析为 Markdown。
     *
     * @param file Word 文件
     * @return 解析后的 Markdown 内容
     */
    @Override
    public Response parseWordFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResponseCodeEnum.WORD_PARSE_FAILED);
        }

        try (InputStream inputStream = file.getInputStream()) {
            WordMarkdownConverter.ParsedWordDocument parsedWordDocument =
                    WordMarkdownConverter.convert(inputStream, file.getOriginalFilename());

            return Response.success(ParseWordFileRspVO.builder()
                    .title(parsedWordDocument.getTitle())
                    .content(parsedWordDocument.getContent())
                    .build());
        } catch (BizException bizException) {
            throw bizException;
        } catch (Exception e) {
            log.error("==> admin 解析 Word 文件失败: ", e);
            throw new BizException(ResponseCodeEnum.WORD_PARSE_FAILED);
        }
    }
}
