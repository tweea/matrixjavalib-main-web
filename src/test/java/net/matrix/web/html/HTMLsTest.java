/*
 * Copyright(C) 2011 matrix
 * All right reserved.
 */
package net.matrix.web.html;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HTMLsTest {
	@Test
	public void fitToLength() {
		String xx = "abc";
		String yy = "abc&nbsp;&nbsp;";
		Assertions.assertThat(HTMLs.fitToLength(xx, 5)).isEqualTo(yy);
	}
}
