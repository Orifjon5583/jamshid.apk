package com.example.haftalikharajat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm";

    private static final String[] UZ_MONTHS = {
            "yanvar", "fevral", "mart", "aprel", "may", "iyun",
            "iyul", "avgust", "sentabr", "oktabr", "noyabr", "dekabr"
    };

    private static final String[] UZ_DAYS = {
            "yakshanba", "dushanba", "seshanba", "chorshanba", "payshanba", "juma", "shanba"
    };

    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN, Locale.US);
        return sdf.format(new Date());
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        return sdf.format(date);
    }

    public static Date parseDate(String dateStr) {
        if (dateStr == null) return new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.US);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String formatHeaderDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int monthIndex = cal.get(Calendar.MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday

        String month = (monthIndex >= 0 && monthIndex < 12) ? UZ_MONTHS[monthIndex] : "";
        String weekDay = (dayOfWeek >= 1 && dayOfWeek <= 7) ? UZ_DAYS[dayOfWeek - 1] : "";

        return day + "-" + month + ", " + weekDay;
    }

    public static String formatUzbekDate(String yyyyMmDd) {
        if (yyyyMmDd == null) return "";
        try {
            Date date = parseDate(yyyyMmDd);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int monthIndex = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            String month = (monthIndex >= 0 && monthIndex < 12) ? UZ_MONTHS[monthIndex] : "";
            return day + "-" + month + " " + year;
        } catch (Exception e) {
            return yyyyMmDd;
        }
    }

    public static String formatRelativeDate(String yyyyMmDd) {
        if (yyyyMmDd == null) return "";
        String today = getTodayDate();
        if (today.equals(yyyyMmDd)) {
            return "Bugun";
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = formatDate(cal.getTime());
        if (yesterday.equals(yyyyMmDd)) {
            return "Kecha";
        }

        return formatUzbekDate(yyyyMmDd);
    }

    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getStartOfWeek(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        // Calendar.MONDAY is 2. If Sunday (1), subtract 6 days.
        int diff = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
        c.add(Calendar.DAY_OF_MONTH, diff);
        return formatDate(c.getTime());
    }

    public static String getEndOfWeek(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int diff = (dayOfWeek == Calendar.SUNDAY) ? 0 : (Calendar.SATURDAY - dayOfWeek + 1);
        c.add(Calendar.DAY_OF_MONTH, diff);
        return formatDate(c.getTime());
    }

    public static String getStartOfMonth(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return formatDate(c.getTime());
    }

    public static String getEndOfMonth(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return formatDate(c.getTime());
    }

    public static String getStartOfYear(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_YEAR, 1);
        return formatDate(c.getTime());
    }

    public static String getEndOfYear(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
        return formatDate(c.getTime());
    }

    public static int getDaysDiffFromToday(String yyyyMmDd) {
        try {
            Date target = parseDate(yyyyMmDd);
            Date today = parseDate(getTodayDate());
            long diffMillis = target.getTime() - today.getTime();
            return (int) (diffMillis / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return 0;
        }
    }
}
