package com.grouprace.core.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatStartTime(String startTimeStr) {
        if (startTimeStr == null) return "Unknown time";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = inputFormat.parse(startTimeStr);
            if (date == null) return startTimeStr;

            Calendar now = Calendar.getInstance();
            Calendar start = Calendar.getInstance();
            start.setTime(date);

            StringBuilder sb = new StringBuilder();

            // Handle Today/Yesterday or Date
            if (isSameDay(now, start)) {
                sb.append("Today");
            } else if (isYesterday(now, start)) {
                sb.append("Yesterday");
            } else {
                // If same year, don't show year
                if (now.get(Calendar.YEAR) == start.get(Calendar.YEAR)) {
                    sb.append(new SimpleDateFormat("MMM d", Locale.getDefault()).format(date));
                } else {
                    sb.append(new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date));
                }
            }

            sb.append(" at ");

            // Handle Time: am/pm, minute=0 not show
            int hour = start.get(Calendar.HOUR);
            if (hour == 0) hour = 12; // 12-hour format
            int minute = start.get(Calendar.MINUTE);
            String amPm = start.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM";

            sb.append(hour);
            if (minute > 0) {
                sb.append(":").append(String.format(Locale.getDefault(), "%02d", minute));
            }
            sb.append(amPm);

            return sb.toString();

        } catch (ParseException e) {
            return startTimeStr;
        }
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isYesterday(Calendar now, Calendar start) {
        Calendar yesterday = (Calendar) now.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        return isSameDay(yesterday, start);
    }
}
