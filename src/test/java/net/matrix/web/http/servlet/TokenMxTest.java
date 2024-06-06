/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

class TokenMxTest {
    @Test
    void testGenerateToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        String token = TokenMx.generateToken(request, "test");
        assertThat(session.getAttribute("test")).isEqualTo(token);
    }

    @Test
    void testGetToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        session.setAttribute("test", "abc");

        assertThat(TokenMx.getToken(request, "test")).isEqualTo("abc");
    }

    @Test
    void testCheckToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        request.setParameter("test", "abc");
        session.setAttribute("test", "abc");

        assertThat(TokenMx.checkToken(request, "test")).isTrue();
    }

    @Test
    void testRemoveToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        session.setAttribute("test", "abc");

        TokenMx.removeToken(request, "test");
        assertThat(session.getAttribute("test")).isNull();
    }
}
