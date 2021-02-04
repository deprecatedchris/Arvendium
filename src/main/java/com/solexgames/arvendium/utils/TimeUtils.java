package com.solexgames.arvendium.utils;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String handleFormat(long input) {
        return DurationFormatUtils.formatDurationWords(input, true, true);
    }

    public static long handleParseTime(String input) {
        if (Character.isLetter(input.charAt(0)) || input.isEmpty()) return -1L;
        long result = 0L;
        StringBuilder number = new StringBuilder();

        // Can't do lambda here because of variable
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str;

                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += handleConvert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        } return result;
    }

    private static long handleConvert(int value, char charType) {
        switch (charType) {
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'w':
                return value * TimeUnit.DAYS.toMillis(7L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return -1L;
        }
    }

    public static String handleFormatDateDiff(Date from, Date to) {
        boolean future = false;

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(from);

        Calendar toDate = Calendar.getInstance();
        toDate.setTime(to);

        if (toDate.equals(fromDate)) return "now";
        if (toDate.after(fromDate)) future = true;

        StringBuilder sb = new StringBuilder();
        int[] types = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
        String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
        int accuracy = 0;

        // Can't do lambda here because of variable
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) break;

            int diff = handleDateDiff(types[i], fromDate, toDate, future);

            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }

        return sb.length() == 0 ? "now" : sb.toString().trim();
    }

    private static int handleDateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int year = Calendar.YEAR;

        int fromYear = fromDate.get(year);
        int toYear = toDate.get(year);

        if (Math.abs(fromYear - toYear) > 100000) toDate.set(year, fromYear + (future ? 100000 : -100000));

        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();

        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }

        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
