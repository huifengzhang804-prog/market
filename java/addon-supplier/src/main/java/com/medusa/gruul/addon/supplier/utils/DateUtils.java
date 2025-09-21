package com.medusa.gruul.addon.supplier.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate convert2Date(String dateTime, DateTimeFormatter dtf) {
        return LocalDate.parse(dateTime, dtf);
    }

    public static LocalDateTime convert2DateTime(String dateTime, DateTimeFormatter dtf) {
        return LocalDateTime.parse(dateTime, dtf);
    }

    public static String convert2String(LocalDate date, DateTimeFormatter dtf) {
        return date.format(dtf);
    }

    public static String convertLocalDateTime2String(LocalDateTime date, DateTimeFormatter dtf) {
        return date.format(dtf);
    }

    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }


    /**
     * 获取当天的23:59:59
     *
     *
     * @return
     */
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.with(LocalTime.MAX);
    }
}
