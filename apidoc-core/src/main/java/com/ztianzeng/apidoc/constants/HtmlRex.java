package com.ztianzeng.apidoc.constants;

import java.util.regex.Pattern;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-07 22:35
 */
public class HtmlRex {
    public static final String HTML_P = "(<p>)([\\s\\S]*?)(</p>)";
    public static final Pattern HTML_P_PATTERN = Pattern.compile(HTML_P);
}