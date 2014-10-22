/*
 * Copyright(C) 2011 matrix
 * All right reserved.
 */
package net.matrix.web.html;

import org.junit.Assert;
import org.junit.Test;

public class HTMLsTest {
	@Test
	public void fitToLength() {
		String xx = "abc";
		String yy = "abc&nbsp;&nbsp;";
		Assert.assertEquals(yy, HTMLs.fitToLength(xx, 5));
	}
}
