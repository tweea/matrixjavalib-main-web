/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.servlet.session;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.matrix.java.util.EnumerationIterable;

/**
 * 参数和请求工具。
 */
public final class HttpServlets {
    /**
     * 阻止实例化。
     */
    private HttpServlets() {
    }

    /**
     * 取得带相同前缀的参数。
     * 返回的结果的参数名已去除前缀。
     * 
     * @param request
     *     请求
     * @param prefix
     *     前缀
     * @return 参数内容
     */
    public static Map<String, Object> getParametersStartingWith(HttpServletRequest request, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<>();
        for (String paramName : new EnumerationIterable<String>(paramNames)) {
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }
}
