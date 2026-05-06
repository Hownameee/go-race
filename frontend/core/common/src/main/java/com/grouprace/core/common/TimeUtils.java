package com.grouprace.core.common;

public class TimeUtils {

    /**
     * Formats duration in seconds to a human-readable string (e.g., "1h 25m 30s").
     * Zero values are omitted unless the entire duration is 0.
     */
    public static String formatDuration(int totalSeconds) {
        if (totalSeconds <= 0) return "0s";

        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Formats a raw date/time string from the database or server into a beautiful relative time (e.g., "3m ago", "2h ago", "Yesterday", "MMM d, yyyy").
     */
    public static String formatRelativeTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "";
        }

        java.util.Date date = null;
        String[] formats = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, java.util.Locale.US);
                if (format.contains("'Z'")) {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                }
                date = sdf.parse(dateStr);
                if (date != null) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        if (date == null) {
            return dateStr;
        }

        long diffMs = System.currentTimeMillis() - date.getTime();
        long diffSeconds = diffMs / 1000;

        if (diffSeconds < 0) {
            return "Just now";
        }

        if (diffSeconds < 60) {
            return "Just now";
        }

        long diffMinutes = diffSeconds / 60;
        if (diffMinutes < 60) {
            return diffMinutes + "m ago";
        }

        long diffHours = diffMinutes / 60;
        if (diffHours < 24) {
            return diffHours + "h ago";
        }

        long diffDays = diffHours / 24;
        if (diffDays < 7) {
            return diffDays + "d ago";
        }

        try {
            java.text.SimpleDateFormat outputSdf = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US);
            return outputSdf.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
