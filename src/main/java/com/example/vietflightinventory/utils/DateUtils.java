// src/main/java/com/example/vietflightinventory/utils/DateUtils.java
package com.example.vietflightinventory.utils;

import com.example.vietflightinventory.constants.AppConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class DateUtils {

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATETIME_FORMAT_FULL, Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static boolean isToday(Date date) {
        if (date == null) return false;

        Calendar today = Calendar.getInstance();
        Calendar dateToCheck = Calendar.getInstance();
        dateToCheck.setTime(date);

        return today.get(Calendar.YEAR) == dateToCheck.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == dateToCheck.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isFuture(Date date) {
        return date != null && date.after(new Date());
    }

    public static boolean isPast(Date date) {
        return date != null && date.before(new Date());
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}