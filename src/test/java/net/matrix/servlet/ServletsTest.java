/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ServletsTest {
    @Test
    public void testCheckIfModifiedSince() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 未设 Header，返回 true，需要传输内容
        assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isTrue();

        // 设置 If-Modified-Since Header
        request.addHeader("If-Modified-Since", new Date().getTime());
        // 文件修改时间比 Header 时间小，文件未修改，返回 false
        assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isFalse();
        // 文件修改时间比 Header 时间大，文件已修改，返回 true，需要传输内容
        assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() + 2000))).isTrue();
    }

    @Test
    public void testCheckIfNoneMatchEtag() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 未设 Header，返回 true，需要传输内容
        assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V1.0")).isTrue();

        // 设置 If-None-Match Header
        request.addHeader("If-None-Match", "V1.0,V1.1");
        // 存在 Etag
        assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V1.0")).isFalse();
        // 不存在 Etag
        assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V2.0")).isTrue();
    }
}
