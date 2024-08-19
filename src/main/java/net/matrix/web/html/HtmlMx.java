/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.html;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.StringUtils;

/**
 * HTML 语言工具。
 */
@ThreadSafe
public final class HtmlMx {
    /**
     * 空格字符。
     */
    public static final String SPACE = "&nbsp;";

    /**
     * 阻止实例化。
     */
    private HtmlMx() {
    }

    /**
     * 将 HTML 文本扩展到指定长度。
     *
     * @param html
     *     HTML 文本。
     * @param length
     *     长度。
     * @return 扩展后的 HTML 文本。
     */
    @Nonnull
    public static String expandToLength(@Nullable String html, int length) {
        if (html == null) {
            return StringUtils.repeat(SPACE, length);
        }

        int htmlLength = html.length();
        if (htmlLength >= length) {
            return html;
        }

        int expandLength = length - htmlLength;
        StringBuilder sb = new StringBuilder(htmlLength + expandLength * SPACE.length());
        sb.append(html);
        for (int i = 0; i < expandLength; i++) {
            sb.append(SPACE);
        }
        return sb.toString();
    }
}
