/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet.filter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.assertj.core.util.introspection.FieldSupport;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LogRequestFilterTest {
    FieldSupport fieldSupport = FieldSupport.extraction();

    @Test
    void testInit()
        throws ServletException {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("enable", "true");
        LogRequestFilter filter = new LogRequestFilter();

        filter.init(filterConfig);
        assertThat(fieldSupport.fieldValue("enabled", boolean.class, filter)).isTrue();
    }

    @Test
    void testDoFilter()
        throws ServletException, IOException {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("enable", "true");
        LogRequestFilter filter = new LogRequestFilter();
        filter.init(filterConfig);
        MockFilterChain filterChain = new MockFilterChain();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);
        assertThat(fieldSupport.fieldValue("enabled", boolean.class, filter)).isTrue();
    }
}
