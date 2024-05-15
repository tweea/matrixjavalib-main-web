/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.HttpHeaders;

/**
 * HTTP 协议的 Servlet 工具。
 */
public final class HttpServletMx {
    /**
     * 阻止实例化。
     */
    private HttpServletMx() {
    }

    /**
     * 获取客户端 User-Agent 请求头。
     * 
     * @param request
     *     HTTP 请求。
     * @return 客户端 User-Agent 请求头。
     */
    public static String getUserAgentHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * 设置客户端缓存过期时间响应头。
     * 
     * @param response
     *     HTTP 响应。
     * @param expiresInSecond
     *     过期时间。
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresInSecond) {
        // HTTP 1.0 header, set a fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + expiresInSecond * 1000);
        // HTTP 1.1 header, set a time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresInSecond);
    }

    /**
     * 设置客户端禁用缓存响应头。
     * 
     * @param response
     *     HTTP 响应。
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        // HTTP 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // HTTP 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    /**
     * 设置客户端 Last-Modified 响应头。
     * 
     * @param response
     *     HTTP 响应。
     * @param lastModified
     *     最后修改时间。
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModified) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
    }

    /**
     * 根据客户端 If-Modified-Since 请求头，计算请求对应的资源是否已被修改。
     * 如果未被修改，返回 false，设置响应状态码为 304。
     * 
     * @param request
     *     HTTP 请求。
     * @param response
     *     HTTP 响应。
     * @param lastModified
     *     最后修改时间。
     * @return 是否已被修改。
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response, long lastModified) {
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (ifModifiedSince == -1 || lastModified >= ifModifiedSince + 1000) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        return false;
    }

    /**
     * 设置客户端 ETag 响应头。
     * 
     * @param response
     *     HTTP 响应。
     * @param etag
     *     资源的 ETag。
     */
    public static void setEtagHeader(HttpServletResponse response, String etag) {
        response.setHeader(HttpHeaders.ETAG, etag);
    }

    /**
     * 根据客户端 If-None-Match 请求头，计算请求对应的资源 ETag 是否已失效。
     * 如果 ETag 未失效，返回 false，设置响应状态码为 304。
     * 
     * @param request
     *     HTTP 请求。
     * @param response
     *     HTTP 响应。
     * @param etag
     *     资源的 ETag。
     * @return 是否已失效。
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
        String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        boolean matched;
        if (ifNoneMatch == null) {
            matched = false;
        } else if ("*".equals(ifNoneMatch)) {
            matched = true;
        } else {
            matched = false;
            StringTokenizer tokenizer = new StringTokenizer(ifNoneMatch, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if (token.equals(etag)) {
                    matched = true;
                    break;
                }
            }
        }
        if (matched) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }

        response.setHeader(HttpHeaders.ETAG, etag);
        return true;
    }

    /**
     * 设置客户端文件名响应头。
     * 
     * @param response
     *     HTTP 响应。
     * @param filename
     *     文件名。
     */
    public static void setFilenameHeader(HttpServletResponse response, String filename) {
        // 中文文件名支持
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + '\"');
    }
}
