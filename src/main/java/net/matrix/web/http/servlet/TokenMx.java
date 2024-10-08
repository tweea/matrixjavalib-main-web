/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 在 HTTP 会话中设置唯一标识，判断 HTTP 请求是否有效。
 */
@ThreadSafe
public final class TokenMx {
    /**
     * 阻止实例化。
     */
    private TokenMx() {
    }

    /**
     * 生成唯一标识。
     *
     * @param request
     *     HTTP 请求。
     * @param key
     *     键名。
     * @return 唯一标识。
     */
    @Nonnull
    public static String generateToken(@Nonnull HttpServletRequest request, @Nonnull String key) {
        HttpSession session = request.getSession();

        String token = UUID.randomUUID().toString();
        session.setAttribute(key, token);
        return token;
    }

    /**
     * 获取已有唯一标识。
     *
     * @param request
     *     HTTP 请求。
     * @param key
     *     键名。
     * @return 唯一标识。
     */
    @Nullable
    public static String getToken(@Nonnull HttpServletRequest request, @Nonnull String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (String) session.getAttribute(key);
    }

    /**
     * 判断唯一标识是否有效。
     *
     * @param request
     *     HTTP 请求。
     * @param key
     *     键名。
     * @return 是否有效。
     */
    public static boolean checkToken(@Nonnull HttpServletRequest request, @Nonnull String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String requestToken = request.getParameter(key);
        if (requestToken == null) {
            return false;
        }

        String token = (String) session.getAttribute(key);
        return requestToken.equals(token);
    }

    /**
     * 删除唯一标识。
     *
     * @param request
     *     HTTP 请求。
     * @param key
     *     键名。
     */
    public static void removeToken(@Nonnull HttpServletRequest request, @Nonnull String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        session.removeAttribute(key);
    }
}
