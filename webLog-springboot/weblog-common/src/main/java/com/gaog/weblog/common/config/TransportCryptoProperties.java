package com.gaog.weblog.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 传输层加解密配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "transport.crypto")
public class TransportCryptoProperties {

    private boolean enabled = true;

    /**
     * AES Key，要求 16/24/32 字节
     */
    private String key = "WebLogTransportKey2026Secret!!@#";
}
