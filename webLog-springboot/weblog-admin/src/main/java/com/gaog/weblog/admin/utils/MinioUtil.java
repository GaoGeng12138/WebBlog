package com.gaog.weblog.admin.utils;

import com.gaog.weblog.admin.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author ZSJ
 * @date 2025/11/27 16:21
 * @description
 */
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    private String getFileAccessBaseUrl() {
        String publicEndpoint = minioProperties.getPublicEndpoint();
        String baseUrl = (publicEndpoint != null && !publicEndpoint.trim().isEmpty())
                ? publicEndpoint
                : minioProperties.getEndpoint();

        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String uploadPath) throws Exception {
        // 判断文件是否为空
        if (file == null || file.getSize() == 0) {
            log.error("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        // 文件的原始名称
        String originalFileName = file.getOriginalFilename();
        // 文件的 Content-Type
        String contentType = file.getContentType();

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");
        // 获取文件的后缀，如 .jpg
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 创建文件夹结构：按年月日分类，如 2025/12/13/
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 根据文件类型进一步分类
        String fileTypeFolder = getFileTypeFolder(suffix);

        // 拼接完整的对象路径
        String objectName = String.format("%s/%s/%s/%s%s", uploadPath, fileTypeFolder, datePath, key, suffix);

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

        // 上传文件至 Minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

        // 返回文件的访问链接
        String url = String.format("%s/%s/%s", getFileAccessBaseUrl(), minioProperties.getBucketName(), objectName);
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }

    /**
     * 根据文件后缀确定文件类型文件夹
     *
     * @param suffix 文件后缀
     * @return 文件类型文件夹名称
     */
    private String getFileTypeFolder(String suffix) {
        if (suffix == null) {
            return "other";
        }

        // 图片类型
        if (suffix.matches("\\.(?i)(jpg|jpeg|png|gif|bmp|webp)$")) {
            return "images";
        }

        // 文档类型
        if (suffix.matches("\\.(?i)(doc|docx|pdf|txt|md)$")) {
            return "documents";
        }

        // 视频类型
        if (suffix.matches("\\.(?i)(mp4|avi|mov|wmv|flv|mkv)$")) {
            return "videos";
        }

        // 音频类型
        if (suffix.matches("\\.(?i)(mp3|wav|flac|aac|m4a)$")) {
            return "audios";
        }

        // 其他类型
        return "others";
    }
}
