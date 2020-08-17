/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpServletsTest {
    @Test
    public void testGetParametersStartingWith() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("pre_a", "aa");
        request.addParameter("pre_b", "bb");
        request.addParameter("c", "c");

        Map<String, Object> result = HttpServlets.getParametersStartingWith(request, "pre_");
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("a", "aa");
        assertThat(result).containsEntry("b", "bb");

        result = HttpServlets.getParametersStartingWith(request, "error_");
        assertThat(result).isEmpty();

        result = HttpServlets.getParametersStartingWith(request, null);
        assertThat(result).hasSize(3);
    }
}
