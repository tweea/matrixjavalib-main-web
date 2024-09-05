/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.matrix.lang.ImpossibleException;

import static net.matrix.data.convert.BinaryStringConverter.BASE64;
import static net.matrix.data.convert.BinaryStringConverter.UTF8;

/**
 * HTTP 协议工具。
 */
@ThreadSafe
public final class HttpMx {
    /**
     * 阻止实例化。
     */
    private HttpMx() {
    }

    /**
     * 构造查询字符串。
     *
     * @param params
     *     参数。
     * @return 查询字符串。
     */
    @Nonnull
    public static String buildQueryString(@Nonnull Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            String key = param.getKey();
            Object value = param.getValue();

            sb.append(key).append('=');
            if (value != null) {
                sb.append(encode(value.toString()));
            }
            sb.append('&');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    @Nonnull
    private static String encode(@Nonnull String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ImpossibleException(e);
        }
    }

    /**
     * 构造 Basic 认证请求头。
     *
     * @param username
     *     用户名。
     * @param password
     *     密码。
     * @return 请求头。
     */
    @Nonnull
    public static String buildBasicAuthorizationHeader(@Nonnull String username, @Nonnull String password) {
        String plain = username + ':' + password;
        return "Basic " + BASE64.toString(UTF8.toBinary(plain));
    }
}
