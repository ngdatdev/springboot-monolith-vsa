package com.vsa.ecommerce.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter REQUEST_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    private DateUtil() {}

    public static String now() {
        return REQUEST_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return REQUEST_TIME_FORMATTER.format(localDateTime);
    }
    
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
