package com.gaog.weblog.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ClassName：WeblogWebApplication
 * version:1.0.0
 *
 * @author: GaoG
 * Date: 2025/12/12
 * Description：
 */
@SpringBootApplication
@ComponentScan({"com.gaog.weblog.*"})
@EnableScheduling
@EnableAspectJAutoProxy
@Slf4j
public class WeblogWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WeblogWebApplication.class, args);

        ConfigurableEnvironment environment = context.getEnvironment();


        String serviceName = environment.getProperty("server.servlet.context-path", "/webLog");


        String port = environment.getProperty("server.port", "8080");

        String loopbackAddress = "127.0.0.1";

        String realIp = loopbackAddress;

        try {
            // 获取本机的 IP 地址
            InetAddress inetAddress = InetAddress.getLocalHost();
            realIp = inetAddress.getHostAddress();

            if ("127.0.0.1".equals(realIp)) {
                realIp = "localhost";
            }

        } catch (UnknownHostException e) {
            log.error("获取本机IP地址失败:", e);
        }

        String localhostInfo = String.format("本地服务地址: http://%s:%s%s", loopbackAddress, port, serviceName);


        String realServiceAddress = String.format("真实IP服务地址: http://%s:%s%s", realIp, port, serviceName);

        // 打印启动信息
        log.info("======================================");
        log.info(localhostInfo);
        log.info(realServiceAddress);
        log.info("启动环境: " + environment.getProperty("spring.profiles.active", "default"));
        log.info("======================================");
    }

}
