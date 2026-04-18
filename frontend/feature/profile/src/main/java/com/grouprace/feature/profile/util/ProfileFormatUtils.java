package com.grouprace.feature.profile.util;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ProfileFormatUtils {
    private static final DecimalFormat DISTANCE_SMALL_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat DISTANCE_LARGE_FORMAT = new DecimalFormat("0.0");
    private static final DecimalFormat WHOLE_NUMBER_FORMAT = new DecimalFormat("0");
    private static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final DateTimeFormatter SHORT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd", Locale.US);
    private static final DateTimeFormatter DATE_RANGE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd", Locale.US);

    private ProfileFormatUtils() {
    }

    public static String formatShortDate(@NonNull String isoDate) {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            return date.format(SHORT_DATE_FORMATTER);
        } catch (Exception ignored) {
            return isoDate;
        }
    }

    public static String formatDateRange(@NonNull String startIsoDate, @NonNull String endIsoDate) {
        try {
            LocalDate start = LocalDate.parse(startIsoDate);
            LocalDate end = LocalDate.parse(endIsoDate);
            return start.format(DATE_RANGE_FORMATTER) + " - " + end.format(DATE_RANGE_FORMATTER);
        } catch (Exception ignored) {
            return startIsoDate + " - " + endIsoDate;
        }
    }

    public static String formatDistance(double distanceKm) {
        DecimalFormat decimalFormat = distanceKm < 1 ? DISTANCE_SMALL_FORMAT : DISTANCE_LARGE_FORMAT;
        return decimalFormat.format(distanceKm) + " km";
    }

    public static String formatElevation(double elevationMeters) {
        return WHOLE_NUMBER_FORMAT.format(elevationMeters) + " m";
    }

    public static String formatActivityCount(double count) {
        if (Math.abs(count - Math.rint(count)) < 0.01d) {
            return String.valueOf((int) Math.rint(count));
        }
        return ONE_DECIMAL_FORMAT.format(count);
    }
}
