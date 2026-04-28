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
}
