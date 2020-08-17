/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static org.assertj.core.api.Assertions.assertThat;

public class HTTPsTest {
    @Test
    public void testEncodeParameterStringWithPrefix() {
        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("name", "foo");
        params.put("age", "1");

        String queryString = HTTPs.encodeParameterStringWithPrefix(params, "search_");
        assertThat(queryString).isEqualTo("search_name=foo&search_age=1");

        // data type is not String
        params.clear();
        params.put("name", "foo");
        params.put("age", 1);
        queryString = HTTPs.encodeParameterStringWithPrefix(params, "search_");
        assertThat(queryString).isEqualTo("search_name=foo&search_age=1");

        // prefix is empty
        queryString = HTTPs.encodeParameterStringWithPrefix(params, "");
        assertThat(queryString).isEqualTo("name=foo&age=1");
    }
}
