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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                    + "?fields=status,country,regionName,city,lat,lon,isp&lang=zh-CN";
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
                double lat = root.path("lat").asDouble(Double.NaN);
                double lon = root.path("lon").asDouble(Double.NaN);

                String displayText = buildDisplayText(country, regionName, city, lat, lon);
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

    private static String buildDisplayText(String country, String regionName, String city, double lat, double lon) {
        if ("中国".equals(country)) {
            String detailedLocation = reverseGeocodeChina(lat, lon, regionName, city);
            if (!detailedLocation.isBlank()) {
                return detailedLocation;
            }
        }

        List<String> parts = new ArrayList<>();
        if (!"中国".equals(country)) {
            appendUniquePart(parts, country);
        }
        appendUniquePart(parts, regionName);
        appendUniquePart(parts, city);
        return String.join(" / ", parts);
    }

    private static String reverseGeocodeChina(double lat, double lon, String fallbackProvince, String fallbackCity) {
        if (Double.isNaN(lat) || Double.isNaN(lon)) {
            return buildChinaFallback(fallbackProvince, fallbackCity);
        }

        HttpURLConnection connection = null;
        try {
            String apiUrl = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&accept-language=zh-CN"
                    + "&zoom=18&addressdetails=1&lat=" + lat + "&lon=" + lon;
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "WebBlog/1.0");

            try (InputStream inputStream = connection.getInputStream()) {
                JsonNode root = OBJECT_MAPPER.readTree(inputStream);
                JsonNode address = root.path("address");
                if (address.isMissingNode() || address.isNull()) {
                    return buildChinaFallback(fallbackProvince, fallbackCity);
                }

                List<String> parts = new ArrayList<>();
                appendUniquePart(parts, pickFirstNonBlank(
                        address.path("state").asText(""),
                        fallbackProvince
                ));
                appendUniquePart(parts, pickFirstNonBlank(
                        address.path("city").asText(""),
                        address.path("municipality").asText(""),
                        address.path("prefecture").asText(""),
                        fallbackCity
                ));
                appendUniquePart(parts, pickFirstNonBlank(
                        address.path("county").asText(""),
                        address.path("district").asText("")
                ));
                appendUniquePart(parts, pickFirstNonBlank(
                        address.path("town").asText(""),
                        address.path("village").asText(""),
                        address.path("suburb").asText(""),
                        address.path("borough").asText(""),
                        address.path("hamlet").asText("")
                ));

                if (parts.isEmpty()) {
                    return buildChinaFallback(fallbackProvince, fallbackCity);
                }

                return String.join(" / ", parts);
            }
        } catch (Exception e) {
            log.debug("经纬度反查中国地址失败: lat={}, lon={}", lat, lon, e);
            return buildChinaFallback(fallbackProvince, fallbackCity);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String buildChinaFallback(String province, String city) {
        List<String> parts = new ArrayList<>();
        appendUniquePart(parts, province);
        appendUniquePart(parts, city);
        return String.join(" / ", parts);
    }

    private static String pickFirstNonBlank(String... values) {
        if (values == null) {
            return "";
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return "";
    }

    private static void appendUniquePart(List<String> parts, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        boolean exists = parts.stream().anyMatch(item -> Objects.equals(item, value));
        if (!exists) {
            parts.add(value);
        }
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
