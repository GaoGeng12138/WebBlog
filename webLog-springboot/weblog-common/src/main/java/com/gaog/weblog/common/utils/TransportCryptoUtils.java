package com.gaog.weblog.common.utils;

import com.gaog.weblog.common.config.TransportCryptoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 前后端传输层敏感数据加解密工具
 */
@Slf4j
@Component
public class TransportCryptoUtils {

    public static final String ENCRYPTED_PREFIX = "ENC::";

    private final TransportCryptoProperties properties;

    private byte[] keyBytes;

    public TransportCryptoUtils(TransportCryptoProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        this.keyBytes = properties.getKey().getBytes(StandardCharsets.UTF_8);
        if (!(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32)) {
            throw new IllegalStateException("transport.crypto.key length must be 16, 24, or 32 bytes");
        }
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENCRYPTED_PREFIX);
    }

    public String encryptIfNecessary(String plainText) {
        if (!isEnabled() || isBlank(plainText) || isEncrypted(plainText)) {
            return plainText;
        }

        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return ENCRYPTED_PREFIX
                    + Base64.getEncoder().encodeToString(iv)
                    + "::"
                    + Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to encrypt transport payload", ex);
        }
    }

    public String decryptIfNecessary(String encryptedText) {
        if (!isEnabled() || isBlank(encryptedText) || !isEncrypted(encryptedText)) {
            return encryptedText;
        }

        try {
            String payload = encryptedText.substring(ENCRYPTED_PREFIX.length());
            String[] parts = payload.split("::");
            if (parts.length != 2) {
                return encryptedText;
            }

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(iv));
            byte[] plainBytes = cipher.doFinal(cipherBytes);

            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.warn("Failed to decrypt transport payload, keep original value", ex);
            return encryptedText;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
