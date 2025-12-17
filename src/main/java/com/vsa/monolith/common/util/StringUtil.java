package com.vsa.monolith.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class StringUtil {

    private StringUtil() {}

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String toSnakeCase(String str) {
        if (isEmpty(str)) return str;
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    
    public static String toCamelCase(String str) {
        if (isEmpty(str)) return str;
        // Basic implementation, for robust use Guava or Apache Commons Text
        StringBuilder result = new StringBuilder();
        String[] parts = str.split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                result.append(part.toLowerCase());
            } else {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }
}
