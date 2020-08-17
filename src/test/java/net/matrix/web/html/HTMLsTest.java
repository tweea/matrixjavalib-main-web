/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
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
