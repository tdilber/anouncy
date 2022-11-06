package com.beyt.anouncy.common.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
    private DateUtil() {
    }

    public static boolean isExpired(Date date, Integer seconds) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, -1 * seconds);
        Date expireDate = instance.getTime();
        return date.compareTo(expireDate) < 0;
    }
}
