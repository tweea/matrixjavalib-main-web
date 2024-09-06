/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet.filter;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SetCharacterEncodingFilterTest {
    @Test
    void testInit()
        throws ServletException {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("encoding", "test");
        filterConfig.addInitParameter("ignore", "yes");
        SetCharacterEncodingFilter filter = new SetCharacterEncodingFilter();

        filter.init(filterConfig);
        assertThat(filter.encoding).isEqualTo("test");
        assertThat(filter.ignore).isTrue();
    }

    @Test
    void testDoFilter()
        throws ServletException, IOException {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("encoding", "test");
        filterConfig.addInitParameter("ignore", "yes");
        SetCharacterEncodingFilter filter = new SetCharacterEncodingFilter();
        filter.init(filterConfig);
        MockFilterChain filterChain = new MockFilterChain();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);
        assertThat(request.getCharacterEncoding()).isEqualTo("test");
    }
}
