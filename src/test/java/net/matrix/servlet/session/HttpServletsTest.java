/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.util.Map;

import org.assertj.core.api.Assertions;
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
		Assertions.assertThat(result).hasSize(2);
		Assertions.assertThat(result).containsEntry("a", "aa");
		Assertions.assertThat(result).containsEntry("b", "bb");

		result = HttpServlets.getParametersStartingWith(request, "error_");
		Assertions.assertThat(result).isEmpty();

		result = HttpServlets.getParametersStartingWith(request, null);
		Assertions.assertThat(result).hasSize(3);
	}
}
