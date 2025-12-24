package com.vsa.ecommerce.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    private NumberUtil() {}

    public static boolean isNullOrZero(Integer number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Long number) {
        return number == null || number == 0L;
    }

    public static BigDecimal round(BigDecimal value, int places) {
        if (value == null) return BigDecimal.ZERO;
        if (places < 0) throw new IllegalArgumentException();
        return value.setScale(places, RoundingMode.HALF_UP);
    }
}
