/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import net.matrix.java.lang.NumberMx;
import net.matrix.java.time.DateTimeFormatterMx;

/**
 * HTTP 协议的 Servlet 工具。
 */
@ThreadSafe
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
    @Nullable
    public static String getUserAgentHeader(@Nonnull HttpServletRequest request) {
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
    public static void setExpiresHeader(@Nonnull HttpServletResponse response, long expiresInSecond) {
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
    public static void setNoCacheHeader(@Nonnull HttpServletResponse response) {
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
    public static void setLastModifiedHeader(@Nonnull HttpServletResponse response, long lastModified) {
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
    public static boolean checkIfModifiedSince(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, long lastModified) {
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
    public static void setEtagHeader(@Nonnull HttpServletResponse response, @Nonnull String etag) {
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
    public static boolean checkIfNoneMatchEtag(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull String etag) {
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
    public static void setAttachmentFilenameHeader(@Nonnull HttpServletResponse response, @Nonnull String filename) {
        // 中文文件名支持
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + '\"');
    }

    /**
     * 设置客户端文件名响应头。
     * 
     * @param response
     *     HTTP 响应。
     * @param filename
     *     文件名。
     */
    public static void setInlineFilenameHeader(@Nonnull HttpServletResponse response, @Nonnull String filename) {
        // 中文文件名支持
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFilename + '\"');
    }

    /**
     * 获取字符串类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @return 参数值。
     */
    @Nullable
    public static String getParameter(@Nonnull HttpServletRequest request, @Nonnull String name) {
        return getParameter(request, name, null);
    }

    /**
     * 获取字符串类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param defaultValue
     *     默认值。
     * @return 参数值。
     */
    @Nullable
    public static String getParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nullable String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }

        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 获取整型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @return 参数值。
     */
    @Nullable
    public static Integer getIntegerParameter(@Nonnull HttpServletRequest request, @Nonnull String name) {
        return getIntegerParameter(request, name, null);
    }

    /**
     * 获取整型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param defaultValue
     *     默认值。
     * @return 参数值。
     */
    @Nullable
    public static Integer getIntegerParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nullable Integer defaultValue) {
        String value = request.getParameter(name);
        return NumberMx.parseInteger(value, defaultValue);
    }

    /**
     * 获取长整型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @return 参数值。
     */
    @Nullable
    public static Long getLongParameter(@Nonnull HttpServletRequest request, @Nonnull String name) {
        return getLongParameter(request, name, null);
    }

    /**
     * 获取长整型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param defaultValue
     *     默认值。
     * @return 参数值。
     */
    @Nullable
    public static Long getLongParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nullable Long defaultValue) {
        String value = request.getParameter(name);
        return NumberMx.parseLong(value, defaultValue);
    }

    /**
     * 获取十进制数值类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @return 参数值。
     */
    @Nullable
    public static BigDecimal getBigDecimalParameter(@Nonnull HttpServletRequest request, @Nonnull String name) {
        return getBigDecimalParameter(request, name, null);
    }

    /**
     * 获取十进制数值类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param defaultValue
     *     默认值。
     * @return 参数值。
     */
    @Nullable
    public static BigDecimal getBigDecimalParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nullable BigDecimal defaultValue) {
        String value = request.getParameter(name);
        return NumberMx.parseBigDecimal(value, defaultValue);
    }

    /**
     * 获取时刻类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param pattern
     *     格式，形式见 {@link java.time.format.DateTimeFormatter}。
     * @return 参数值。
     */
    @Nullable
    public static Instant getInstantParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nonnull String pattern) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return DateTimeFormatterMx.parseInstant(value, pattern);
    }

    /**
     * 获取本地日期类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param pattern
     *     格式，形式见 {@link java.time.format.DateTimeFormatter}。
     * @return 参数值。
     */
    @Nullable
    public static LocalDate getLocalDateParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nonnull String pattern) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return DateTimeFormatterMx.parseLocalDate(value, pattern);
    }

    /**
     * 获取本地时间类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param pattern
     *     格式，形式见 {@link java.time.format.DateTimeFormatter}。
     * @return 参数值。
     */
    @Nullable
    public static LocalTime getLocalTimeParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nonnull String pattern) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return DateTimeFormatterMx.parseLocalTime(value, pattern);
    }

    /**
     * 获取本地日期时间类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     参数名。
     * @param pattern
     *     格式，形式见 {@link java.time.format.DateTimeFormatter}。
     * @return 参数值。
     */
    @Nullable
    public static LocalDateTime getLocalDateTimeParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nonnull String pattern) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return DateTimeFormatterMx.parseLocalDateTime(value, pattern);
    }

    /**
     * 获取字符串类型的所有请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @return 所有参数。
     */
    @Nonnull
    public static Map<String, String> getParameterMap(@Nonnull HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        Map<String, String> result = Maps.newLinkedHashMapWithExpectedSize(parameterMap.size());
        for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
            String key = parameterEntry.getKey();
            String value = parameterEntry.getValue()[0];

            result.put(key, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return result;
    }

    /**
     * 获取分页请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param pageSizeName
     *     分页大小参数名。
     * @param pageNumberName
     *     分页序号参数名。
     * @return 分页参数。
     */
    @Nonnull
    public static Pageable getPageable(@Nonnull HttpServletRequest request, @Nonnull String pageSizeName, @Nonnull String pageNumberName) {
        int pageSize = getIntegerParameter(request, pageSizeName, 0);
        int pageNumber = getIntegerParameter(request, pageNumberName, 0);

        if (pageSize == 0) {
            return Pageable.unpaged();
        }

        return PageRequest.of(pageNumber, pageSize);
    }

    /**
     * 获取请求头，如果请求头内容为空，改为获取同名字符串类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     请求头/参数名。
     * @return 请求头/参数值。
     */
    @Nullable
    public static String getHeaderOrParameter(@Nonnull HttpServletRequest request, @Nonnull String name) {
        return getHeaderOrParameter(request, name, null);
    }

    /**
     * 获取请求头，如果请求头内容为空，改为获取同名字符串类型请求参数。
     * 
     * @param request
     *     HTTP 请求。
     * @param name
     *     请求头/参数名。
     * @param defaultValue
     *     默认值。
     * @return 请求头/参数值。
     */
    @Nullable
    public static String getHeaderOrParameter(@Nonnull HttpServletRequest request, @Nonnull String name, @Nullable String defaultValue) {
        String value = request.getHeader(name);
        if (value == null) {
            value = request.getParameter(name);
        }
        if (value == null) {
            return defaultValue;
        }

        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
