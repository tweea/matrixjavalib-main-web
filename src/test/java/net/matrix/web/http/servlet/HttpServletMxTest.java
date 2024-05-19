/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.net.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpServletMxTest {
    public static final String ISO_INSTANT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    public static final String ISO_TIME_FORMAT = "HH:mm:ss";

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    public void testGetUserAgentHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.USER_AGENT, "test");

        assertThat(HttpServletMx.getUserAgentHeader(request)).isEqualTo("test");
    }

    @Test
    public void testSetExpiresHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletMx.setExpiresHeader(response, 1);
        assertThat(response.getDateHeader(HttpHeaders.EXPIRES)).isGreaterThan(0);
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL)).startsWith("private");
    }

    @Test
    public void testSetNoCacheHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletMx.setNoCacheHeader(response);
        assertThat(response.getDateHeader(HttpHeaders.EXPIRES)).isEqualTo(0);
        assertThat(response.getHeader(HttpHeaders.PRAGMA)).startsWith("no-cache");
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL)).startsWith("no-cache");
    }

    @Test
    public void testSetLastModifiedHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletMx.setLastModifiedHeader(response, System.currentTimeMillis());
        assertThat(response.getDateHeader(HttpHeaders.LAST_MODIFIED)).isGreaterThan(0);
    }

    @Test
    public void testCheckIfModifiedSince() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 未设 Header，返回 true，需要传输内容
        assertThat(HttpServletMx.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isTrue();

        // 设置 If-Modified-Since Header
        request.addHeader("If-Modified-Since", new Date().getTime());
        // 文件修改时间比 Header 时间小，文件未修改，返回 false
        assertThat(HttpServletMx.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isFalse();
        // 文件修改时间比 Header 时间大，文件已修改，返回 true，需要传输内容
        assertThat(HttpServletMx.checkIfModifiedSince(request, response, (new Date().getTime() + 2000))).isTrue();
    }

    @Test
    public void testSetEtagHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletMx.setEtagHeader(response, "test");
        assertThat(response.getHeader(HttpHeaders.ETAG)).isEqualTo("test");
    }

    @Test
    public void testCheckIfNoneMatchEtag() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 未设 Header，返回 true，需要传输内容
        assertThat(HttpServletMx.checkIfNoneMatchEtag(request, response, "V1.0")).isTrue();

        // 设置 If-None-Match Header
        request.addHeader("If-None-Match", "V1.0,V1.1");
        // 存在 Etag
        assertThat(HttpServletMx.checkIfNoneMatchEtag(request, response, "V1.0")).isFalse();
        // 不存在 Etag
        assertThat(HttpServletMx.checkIfNoneMatchEtag(request, response, "V2.0")).isTrue();
    }

    @Test
    public void testSetFilenameHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpServletMx.setFilenameHeader(response, "test");
        assertThat(response.getHeader(HttpHeaders.CONTENT_DISPOSITION)).startsWith("attachment");
    }

    @Test
    public void testGetParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getParameter(request, "abc")).isEqualTo("123");
        assertThat(HttpServletMx.getParameter(request, "xyz")).isNull();
    }

    @Test
    public void testGetParameter_defaultValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getParameter(request, "abc", "456")).isEqualTo("123");
        assertThat(HttpServletMx.getParameter(request, "xyz", "456")).isEqualTo("456");
    }

    @Test
    public void testGetIntegerParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getIntegerParameter(request, "abc")).isEqualTo(123);
        assertThat(HttpServletMx.getIntegerParameter(request, "xyz")).isNull();
    }

    @Test
    public void testGetIntegerParameter_defaultValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getIntegerParameter(request, "abc", 456)).isEqualTo(123);
        assertThat(HttpServletMx.getIntegerParameter(request, "xyz", 456)).isEqualTo(456);
    }

    @Test
    public void testGetLongParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getLongParameter(request, "abc")).isEqualTo(123);
        assertThat(HttpServletMx.getLongParameter(request, "xyz")).isNull();
    }

    @Test
    public void testGetLongParameter_defaultValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getLongParameter(request, "abc", 456L)).isEqualTo(123);
        assertThat(HttpServletMx.getLongParameter(request, "xyz", 456L)).isEqualTo(456);
    }

    @Test
    public void testGetBigDecimalParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getBigDecimalParameter(request, "abc")).isEqualTo("123");
        assertThat(HttpServletMx.getBigDecimalParameter(request, "xyz")).isNull();
    }

    @Test
    public void testGetBigDecimalParameter_defaultValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "123");

        assertThat(HttpServletMx.getBigDecimalParameter(request, "abc", new BigDecimal(456))).isEqualTo("123");
        assertThat(HttpServletMx.getBigDecimalParameter(request, "xyz", new BigDecimal(456))).isEqualTo("456");
    }

    @Test
    public void testGetInstantParameter() {
        LocalDateTime datetime = LocalDateTime.of(2011, 12, 1, 12, 13, 14);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "2011-12-01T12:13:14Z");

        assertThat(LocalDateTime.ofInstant(HttpServletMx.getInstantParameter(request, "abc", ISO_INSTANT_FORMAT), ZoneId.of("Z"))).isEqualTo(datetime);
        assertThat(HttpServletMx.getInstantParameter(request, "xyz", ISO_INSTANT_FORMAT)).isNull();
    }

    @Test
    public void testGetLocalDateParameter() {
        LocalDate date = LocalDate.of(2011, 12, 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "2011-12-01");

        assertThat(HttpServletMx.getLocalDateParameter(request, "abc", ISO_DATE_FORMAT)).isEqualTo(date);
        assertThat(HttpServletMx.getLocalDateParameter(request, "xyz", ISO_DATE_FORMAT)).isNull();
    }

    @Test
    public void testGetLocalTimeParameter() {
        LocalTime time = LocalTime.of(12, 13, 14);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "12:13:14");

        assertThat(HttpServletMx.getLocalTimeParameter(request, "abc", ISO_TIME_FORMAT)).isEqualTo(time);
        assertThat(HttpServletMx.getLocalTimeParameter(request, "xyz", ISO_TIME_FORMAT)).isNull();
    }

    @Test
    public void testGetLocalDateTimeParameter() {
        LocalDateTime datetime = LocalDateTime.of(2011, 12, 1, 12, 13, 14);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("abc", "2011-12-01T12:13:14");

        assertThat(HttpServletMx.getLocalDateTimeParameter(request, "abc", ISO_DATETIME_FORMAT)).isEqualTo(datetime);
        assertThat(HttpServletMx.getLocalDateTimeParameter(request, "xyz", ISO_DATETIME_FORMAT)).isNull();
    }

    @Test
    public void testGetParameterMap() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("pre_a", "aa");
        request.addParameter("pre_b", "bb");
        request.addParameter("c", "c");

        Map<String, String> parameterMap = HttpServletMx.getParameterMap(request);
        assertThat(parameterMap).hasSize(3);
        assertThat(parameterMap).containsEntry("pre_a", "aa");
        assertThat(parameterMap).containsEntry("pre_b", "bb");
        assertThat(parameterMap).containsEntry("c", "c");
    }

    @Test
    public void testGetPageable() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("a", "1");
        request.addParameter("b", "2");

        Pageable pageable = HttpServletMx.getPageable(request, "a", "b");
        assertThat(pageable.getPageSize()).isEqualTo(1);
        assertThat(pageable.getPageNumber()).isEqualTo(2);
    }
}
