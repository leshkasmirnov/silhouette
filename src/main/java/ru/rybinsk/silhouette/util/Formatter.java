/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class Formatter {

    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
    private static final String SIMPLE_TIME_FORMAT = "hh:mm:ss";
    private static final String WHOLE_FLOAT_FORMAT = "Количество занятий: %.0f";
    private static final String NOT_WHOLE_FLOAT_FORMAT = "Количество занятий: %.1f";

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat(SIMPLE_TIME_FORMAT);

    public static String formatDate(Date date) {
        return date != null ? DATE_FORMATTER.format(date) : "";
    }

    public static int parseHours(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[0]);
    }

    public static int parseMinutes(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[1]);
    }

    public static String formatFloat(float v) {
        String result;
        if (v == Math.round(v)) {
            result = String.format(WHOLE_FLOAT_FORMAT, v);
        } else {
            result = String.format(NOT_WHOLE_FLOAT_FORMAT, v);
        }
        return result;
    }

}
