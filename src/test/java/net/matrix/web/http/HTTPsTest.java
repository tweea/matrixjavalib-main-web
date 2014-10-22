/*
 * 版权所有 2012 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

public class HTTPsTest {
	@Test
	public void encodeParameterStringWithPrefix() {
		Map<String, Object> params = Maps.newLinkedHashMap();
		params.put("name", "foo");
		params.put("age", "1");

		String queryString = HTTPs.encodeParameterStringWithPrefix(params, "search_");
		Assert.assertEquals("search_name=foo&search_age=1", queryString);

		// data type is not String
		params.clear();
		params.put("name", "foo");
		params.put("age", 1);
		queryString = HTTPs.encodeParameterStringWithPrefix(params, "search_");
		Assert.assertEquals("search_name=foo&search_age=1", queryString);

		// prefix is empty
		queryString = HTTPs.encodeParameterStringWithPrefix(params, "");
		Assert.assertEquals("name=foo&age=1", queryString);
	}
}
