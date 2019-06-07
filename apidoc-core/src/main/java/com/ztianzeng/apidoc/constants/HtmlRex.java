package com.ztianzeng.apidoc.constants;

import java.util.regex.Pattern;

/**
 * HTML 正则
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-07 22:35
 */
public final class HtmlRex {
    private HtmlRex() {
    }

    /**
     * 匹配<p>标签
     */
    private static final String HTML_P = "(<p>)([\\s\\S]*?)(</p>)";
    public static final Pattern HTML_P_PATTERN = Pattern.compile(HTML_P);
}