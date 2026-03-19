package com.gaog.weblog.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ZSJ
 * @date 2025/11/27 16:20
 * @description
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {
    private String endpoint;
    private String publicEndpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
