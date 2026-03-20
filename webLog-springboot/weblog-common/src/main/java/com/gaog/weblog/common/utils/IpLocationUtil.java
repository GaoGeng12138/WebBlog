package com.gaog.weblog.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaoge
 * @description IP 归属地解析工具
 */
@Slf4j
public class IpLocationUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, LocationInfo> LOCATION_CACHE = new ConcurrentHashMap<>();

    private IpLocationUtil() {
    }

    public static String resolveLocation(String ipAddress) {
        return resolveLocationInfo(ipAddress).getDisplayText();
    }

    public static LocationInfo resolveLocationInfo(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return LocationInfo.unknown();
        }

        return LOCATION_CACHE.computeIfAbsent(ipAddress, IpLocationUtil::doResolveLocation);
    }

    private static LocationInfo doResolveLocation(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);

            if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress()) {
                return LocationInfo.local("本机访问");
            }

            if (inetAddress.isSiteLocalAddress()) {
                return LocationInfo.local("局域网地址");
            }

            if (inetAddress.isLinkLocalAddress()) {
                return LocationInfo.local("链路本地地址");
            }
        } catch (Exception e) {
            log.debug("解析 IP 基础类型失败: {}", ipAddress, e);
        }

        LocationInfo locationInfo = queryRemoteLocation(ipAddress);
        return locationInfo == null ? LocationInfo.publicNetwork() : locationInfo;
    }

    private static LocationInfo queryRemoteLocation(String ipAddress) {
        HttpURLConnection connection = null;
        try {
            String apiUrl = "http://ip-api.com/json/" + URLEncoder.encode(ipAddress, StandardCharsets.UTF_8)
                    + "?fields=status,country,regionName,city,isp&lang=zh-CN";
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                JsonNode root = OBJECT_MAPPER.readTree(inputStream);
                if (!"success".equalsIgnoreCase(root.path("status").asText())) {
                    return null;
                }

                String country = root.path("country").asText("");
                String regionName = root.path("regionName").asText("");
                String city = root.path("city").asText("");

                String displayText = buildDisplayText(country, regionName, city);
                if (displayText.isBlank()) {
                    return null;
                }

                return new LocationInfo(country, regionName, city, displayText);
            }
        } catch (Exception e) {
            log.debug("远程解析 IP 归属地失败: {}", ipAddress, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String buildDisplayText(String country, String regionName, String city) {
        StringBuilder builder = new StringBuilder();
        if (!"中国".equals(country)) {
            appendPart(builder, country);
        }
        appendPart(builder, regionName);
        appendPart(builder, city);
        return builder.toString();
    }

    private static void appendPart(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        if (builder.length() > 0) {
            builder.append(" / ");
        }
        builder.append(value);
    }

    public static final class LocationInfo {
        private final String country;
        private final String province;
        private final String city;
        private final String displayText;

        private LocationInfo(String country, String province, String city, String displayText) {
            this.country = country;
            this.province = province;
            this.city = city;
            this.displayText = displayText;
        }

        public static LocationInfo unknown() {
            return new LocationInfo("", "", "", "未知地址");
        }

        public static LocationInfo publicNetwork() {
            return new LocationInfo("", "", "", "公网地址");
        }

        public static LocationInfo local(String displayText) {
            return new LocationInfo("", "", "", displayText);
        }

        public String getCountry() {
            return country;
        }

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getDisplayText() {
            return displayText;
        }
    }
}
