/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.html;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlMxTest {
    @Test
    void testExpandToLength() {
        String xx = "abc";
        String yy = "abc&nbsp;&nbsp;";

        assertThat(HtmlMx.expandToLength(xx, 5)).isEqualTo(yy);
    }
}
