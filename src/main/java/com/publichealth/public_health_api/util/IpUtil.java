package com.publichealth.public_health_api.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final int IP_LENGTH_MAX = 15;

    /**
     * 获取客户端真实IP地址
     * 支持通过代理服务器（Nginx、Apache等）转发的场景
     *
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多级代理的情况，X-Forwarded-For可能包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // IPv6 localhost 转换为 IPv4 localhost
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }

        return ip;
    }

    /**
     * 判断是否为内网IP
     *
     * @param ip IP地址
     * @return true-内网IP，false-外网IP
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // 本地回环地址
        if (LOCALHOST_IPV4.equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return true;
        }

        // 检查IP格式
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);

            // 10.0.0.0 - 10.255.255.255
            if (first == 10) {
                return true;
            }

            // 172.16.0.0 - 172.31.255.255
            if (first == 172 && second >= 16 && second <= 31) {
                return true;
            }

            // 192.168.0.0 - 192.168.255.255
            if (first == 192 && second == 168) {
                return true;
            }

            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取IP位置（简单区分内网/外网）
     *
     * @param ip IP地址
     * @return 位置描述
     */
    public static String getLocation(String ip) {
        if (isInternalIp(ip)) {
            return "内网";
        }
        return "外网";
    }
}
