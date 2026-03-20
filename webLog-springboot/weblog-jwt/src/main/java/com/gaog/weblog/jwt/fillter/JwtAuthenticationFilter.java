package com.gaog.weblog.jwt.fillter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.TransportCryptoUtils;
import com.gaog.weblog.jwt.exception.UsernameOrPasswordNullException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 22:36
 * @Version: 1.0
 * @Description:
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final TransportCryptoUtils transportCryptoUtils;

    /**
     * 指定用户登录的访问地址
     */
    public JwtAuthenticationFilter(TransportCryptoUtils transportCryptoUtils) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.transportCryptoUtils = transportCryptoUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        // 解析提交的 JSON 数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());
        JsonNode usernameNode = jsonNode.get("username");
        JsonNode passwordNode = jsonNode.get("password");

        //判断用户名、密码是否为空
        if (Objects.isNull(usernameNode) || Objects.isNull(passwordNode)
                || StringUtils.isBlank(usernameNode.asText()) || StringUtils.isBlank(passwordNode.asText())) {
            throw new UsernameOrPasswordNullException("用户名或密码不能为空");
        }

        final String username;
        final String password;
        try {
            username = transportCryptoUtils.decryptIfNecessary(usernameNode.asText());
            password = transportCryptoUtils.decryptIfNecessary(passwordNode.asText());
        } catch (BizException ex) {
            throw new AuthenticationServiceException(ex.getErrorMessage(), ex);
        }


        // 将用户名、密码封装到 Token 中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

    }
}
