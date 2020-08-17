/*
 * Copyright(C) 2011 matrix
 * All right reserved.
 */
package net.matrix.web.html;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HTMLsTest {
    @Test
    public void testFitToLength() {
        String xx = "abc";
        String yy = "abc&nbsp;&nbsp;";

        assertThat(HTMLs.fitToLength(xx, 5)).isEqualTo(yy);
    }
}
