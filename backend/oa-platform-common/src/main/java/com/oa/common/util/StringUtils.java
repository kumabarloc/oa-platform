package com.oa.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.UUID;

/**
 * 字符串工具类
 */
public class StringUtils {

    private static final String UNDERLINE = "_";
    private static final char CAMEL_CASE_SEPARATOR = '_';

    /**
     * 下划线转驼峰
     */
    public static String underlineToCamel(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar == CAMEL_CASE_SEPARATOR) {
                upperCase = true;
            } else {
                result.append(upperCase ? Character.toUpperCase(currentChar) : currentChar);
                upperCase = false;
            }
        }
        return result.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderline(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                if (i > 0) {
                    result.append(UNDERLINE);
                }
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * 生成UUID（无横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 格式化字符串
     */
    public static String format(String template, Object... params) {
        return StrUtil.format(template, params);
    }
}