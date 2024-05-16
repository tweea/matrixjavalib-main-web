/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static net.matrix.data.convert.BinaryStringConverter.BASE64;
import static net.matrix.data.convert.BinaryStringConverter.UTF8;

/**
 * HTTP 协议工具。
 */
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
    public static String buildQueryString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            String key = param.getKey();
            Object value = param.getValue();

            sb.append(key).append('=');
            if (value != null) {
                sb.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
            }
            sb.append('&');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
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
    public static String buildBasicAuthorizationHeader(String username, String password) {
        String plain = username + ':' + password;
        return "Basic " + BASE64.toString(UTF8.toBinary(plain));
    }
}
