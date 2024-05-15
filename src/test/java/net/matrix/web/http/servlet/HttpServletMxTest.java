/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.net.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpServletMxTest {
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
}
