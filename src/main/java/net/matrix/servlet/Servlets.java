/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.servlet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;

/**
 * Servlet 工具。
 */
public final class Servlets {
    /**
     * 阻止实例化。
     */
    private Servlets() {
    }

    /**
     * 获取客户端 UserAgent 字符串。
     * 
     * @param request
     *     请求
     * @return 客户端 UserAgent 字符串
     */
    public static String getUserAgent(final HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * 设置客户端缓存过期时间的 Header。
     * 
     * @param response
     *     响应
     * @param expiresSeconds
     *     过期时间
     */
    public static void setExpiresHeader(final HttpServletResponse response, final long expiresSeconds) {
        // Http 1.0 header, set a fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + expiresSeconds * 1000);
        // Http 1.1 header, set a time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的 Header。
     * 
     * @param response
     *     响应
     */
    public static void setNoCacheHeader(final HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // Http 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    /**
     * 设置 LastModified Header。
     * 
     * @param response
     *     响应
     * @param lastModified
     *     最后修改时间
     */
    public static void setLastModifiedHeader(final HttpServletResponse response, final long lastModified) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
    }

    /**
     * 根据浏览器 If-Modified-Since Header，计算文件是否已被修改。
     * 如果无修改，返回 false，设置 304 not modify status。
     * 
     * @param request
     *     请求
     * @param response
     *     响应
     * @param lastModified
     *     内容的最后修改时间
     * @return 文件是否已被修改
     */
    public static boolean checkIfModifiedSince(final HttpServletRequest request, final HttpServletResponse response, final long lastModified) {
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (ifModifiedSince != -1 && lastModified < ifModifiedSince + 1000) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
        return true;
    }

    /**
     * 设置 ETag Header。
     * 
     * @param response
     *     响应
     * @param etag
     *     内容的 ETag
     */
    public static void setEtag(final HttpServletResponse response, final String etag) {
        response.setHeader(HttpHeaders.ETAG, etag);
    }

    /**
     * 根据浏览器 If-None-Match Header，计算 ETag 是否已失效。
     * 如果 ETag 有效，返回 false，设置 304 not modify status。
     * 
     * @param request
     *     请求
     * @param response
     *     响应
     * @param etag
     *     内容的 ETag
     * @return 是否已失效
     */
    public static boolean checkIfNoneMatchEtag(final HttpServletRequest request, final HttpServletResponse response, final String etag) {
        String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if ("*".equals(headerValue)) {
                conditionSatisfied = true;
            } else {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken().trim();
                    if (currentToken.equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader(HttpHeaders.ETAG, etag);
                return false;
            }
        }
        return true;
    }

    /**
     * 设置让浏览器弹出下载对话框的 Header。
     * 
     * @param response
     *     响应
     * @param filename
     *     下载后的文件名
     */
    public static void setFileDownloadHeader(final HttpServletResponse response, final String filename) {
        // 中文文件名支持
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + '\"');
    }
}
