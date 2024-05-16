/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpMxTest {
    @Test
    public void testBuildQueryString() {
        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("name", "foo");
        params.put("age", 1);
        params.put("xyz", null);

        String queryString = HttpMx.buildQueryString(params);
        assertThat(queryString).isEqualTo("name=foo&age=1&xyz=");
    }

    @Test
    public void testBuildBasicAuthorizationHeader() {
        String header = HttpMx.buildBasicAuthorizationHeader("a", "b");
        assertThat(header).startsWith("Basic");
    }
}
