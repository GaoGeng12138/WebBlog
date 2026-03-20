package com.gaog.weblog.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.common.utils.TransportCryptoUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * 在参数绑定和校验前统一解密 JSON 请求体中的传输密文字段
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransportDecryptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final TransportCryptoUtils transportCryptoUtils;

    public TransportDecryptionFilter(ObjectMapper objectMapper, TransportCryptoUtils transportCryptoUtils) {
        this.objectMapper = objectMapper;
        this.transportCryptoUtils = transportCryptoUtils;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!hasJsonBody(request) || !shouldDecryptTransportBody(request)) {
            return true;
        }

        String method = request.getMethod();
        return !("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        byte[] originalBody = StreamUtils.copyToByteArray(request.getInputStream());
        if (originalBody.length == 0) {
            filterChain.doFilter(request, response);
            return;
        }

        byte[] requestBody = originalBody;
        try {
            JsonNode rootNode = objectMapper.readTree(originalBody);
            JsonNode decryptedNode = decryptNode(rootNode);
            requestBody = objectMapper.writeValueAsBytes(decryptedNode);
        } catch (BizException ex) {
            writeDecryptErrorResponse(response, ex);
            return;
        } catch (Exception ex) {
            // 非 JSON 或无需处理的内容，保持原始请求体继续向下传递
        }

        filterChain.doFilter(new CachedBodyHttpServletRequest(request, requestBody), response);
    }

    private JsonNode decryptNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return node;
        }

        if (node.isTextual()) {
            String value = node.asText();
            String decryptedValue = transportCryptoUtils.decryptIfNecessary(value);
            return equalsText(value, decryptedValue) ? node : TextNode.valueOf(decryptedValue);
        }

        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayNode.set(i, decryptNode(arrayNode.get(i)));
            }
            return arrayNode;
        }

        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                objectNode.set(entry.getKey(), decryptNode(entry.getValue()));
            }
        }

        return node;
    }

    private boolean hasJsonBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private boolean shouldDecryptTransportBody(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("X-Transport-Encrypted"));
    }

    private boolean equalsText(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }

    private void writeDecryptErrorResponse(HttpServletResponse response, BizException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Response.fail(ex)));
    }
}
