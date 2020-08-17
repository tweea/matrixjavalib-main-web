/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.web.html;

import org.apache.commons.lang3.StringUtils;

/**
 * HTML 相关工具。
 */
public final class HTMLs {
    /**
     * 空格。
     */
    public static final String SPACE = "&nbsp;";

    /**
     * 阻止实例化。
     */
    private HTMLs() {
    }

    /**
     * 将 HTML 文本适配到指定长度。
     * 
     * @param html
     *     HTML 文本
     * @param length
     *     目标长度
     * @return 适配结果
     */
    public static String fitToLength(final String html, final int length) {
        if (html == null) {
            return StringUtils.repeat(SPACE, length);
        }
        int len = html.length();
        if (len >= length) {
            return html;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(html);
        for (int i = 0; i < length - len; i++) {
            sb.append(SPACE);
        }
        return sb.toString();
    }
}
