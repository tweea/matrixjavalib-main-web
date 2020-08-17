/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 在会话中设置标示。
 */
public final class Tokens {
    /**
     * 阻止实例化。
     */
    private Tokens() {
    }

    /**
     * 产生新的标示。
     * 
     * @param request
     *     请求
     * @param key
     *     主键
     * @return 标示
     */
    public static String generateToken(final HttpServletRequest request, final String key) {
        String token = UUID.randomUUID().toString();
        request.getSession(true).setAttribute(key, token);
        return token;
    }

    /**
     * 获取已有标示。
     * 
     * @param request
     *     请求
     * @param key
     *     主键
     * @return 标示
     */
    public static String getToken(final HttpServletRequest request, final String key) {
        return (String) request.getSession(true).getAttribute(key);
    }

    /**
     * 判断标示是否有效。
     * 
     * @param request
     *     请求
     * @param key
     *     主键
     * @return 是否有效
     */
    public static boolean isTokenValid(final HttpServletRequest request, final String key) {
        String token = request.getParameter(key);
        if (StringUtils.isBlank(token)) {
            return false;
        }
        String session = (String) request.getSession(true).getAttribute(key);
        return token.equals(session);
    }

    /**
     * 删除标示。
     * 
     * @param request
     *     请求
     * @param key
     *     主键
     */
    public static void removeToken(final HttpServletRequest request, final String key) {
        request.getSession(true).removeAttribute(key);
    }
}
