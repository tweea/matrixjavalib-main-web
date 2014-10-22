/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class HttpServletsTest {
	@Test
	public void getParametersStartingWith() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pre_a", "aa");
		request.addParameter("pre_b", "bb");
		request.addParameter("c", "c");
		Map<String, Object> result = HttpServlets.getParametersStartingWith(request, "pre_");
		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.keySet().contains("a"));
		Assert.assertTrue(result.keySet().contains("b"));
		Assert.assertTrue(result.values().contains("aa"));
		Assert.assertTrue(result.values().contains("bb"));

		result = HttpServlets.getParametersStartingWith(request, "error_");
		Assert.assertEquals(0, result.size());

		result = HttpServlets.getParametersStartingWith(request, null);
		Assert.assertEquals(3, result.size());
	}
}
