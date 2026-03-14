package com.gaog.weblog.admin.config;

import com.gaog.weblog.jwt.config.JwtAuthenticationSecurityConfig;
import com.gaog.weblog.jwt.fillter.TokenAuthenticationFilter;
import com.gaog.weblog.jwt.handler.RestAccessDeniedHandler;
import com.gaog.weblog.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 21:39
 * @Version: 1.0
 * @Description: Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;
    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;


    /**
     * 在 configure() 方法中，首先禁用了 CSRF（Cross-Site Request Forgery）攻击防护。在前后端分离的情况下，通常不需要启用 CSRF 防护。
     * 同时，还禁用了表单登录，并应用了 JWT 相关的配置类 JwtAuthenticationSecurityConfig。最后，配置会话管理这块，将会话策略设置为无状态（STATELESS），
     * 适用于前后端分离的情况，无需创建会话
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*
         * 配置 HTTP 安全性设置
         * 这里主要进行安全认证和授权的配置
         * 包括禁用 csrf、配置表单登录、设置认证规则和会话管理策略
         */
        // 禁用 csrf 跨站请求伪造保护
        http.csrf().disable().
                // 禁用表单登录功能，因为我们使用的是 JWT 认证方式
                        formLogin().disable()
                // 设置用户登录认证相关配置
                .apply(jwtAuthenticationSecurityConfig)
                .and()
                .authorizeHttpRequests()
                // 认证所有以 /admin 为前缀的 URL 资源
                .mvcMatchers("/admin/**").authenticated()
                // 其他都需要放行，无需认证
                .anyRequest().permitAll()
                .and()
                // 处理用户未登录访问受保护的资源的情况
                .httpBasic().authenticationEntryPoint(authEntryPoint)
                .and()
                // 处理登录成功后访问受保护的资源，但是权限不够的情况
                .exceptionHandling().accessDeniedHandler(deniedHandler)
                .and()
                // 前后端分离，无需创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 将 Token 校验过滤器添加到用户认证过滤器之前
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
    }

    /**
     * Token 校验过滤器
     *
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
}


