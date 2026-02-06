package com.publichealth.public_health_api.context;

/**
 * 用户上下文
 * 使用ThreadLocal存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static String getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 清除当前线程的用户信息
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
    }
}
