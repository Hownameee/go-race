package com.grouprace.core.common;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility to export coordinates to a GPX 1.1 file.
 */
public class GpxExporter {

    private static final String GPX_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx version=\"1.1\" creator=\"GoRace\"\n" +
            "  xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
            "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n";

    private static final String GPX_FOOTER = "</gpx>";

    /**
     * Exports the given route to a .gpx file in the app's cache directory.
     * @return The generated GPX file, or null if an error occurred.
     */
    public static File export(Context context, String name, List<double[]> routeCoordinates) {
        if (name == null) return null;

        File cacheDir = new File(context.getCacheDir(), "routes");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "route_" + System.currentTimeMillis() + ".gpx";
        File file = new File(cacheDir, fileName);

        StringBuilder sb = new StringBuilder();
        sb.append(GPX_HEADER);
        sb.append("  <metadata>\n");
        sb.append("    <name>").append(escapeXml(name)).append("</name>\n");
        sb.append("    <time>").append(getIso8601Timestamp()).append("</time>\n");
        sb.append("  </metadata>\n");

        sb.append("  <trk>\n");
        sb.append("    <name>").append(escapeXml(name)).append("</name>\n");
        sb.append("    <trkseg>\n");

        if (routeCoordinates != null) {
            for (double[] coord : routeCoordinates) {
                // coordinates are [lng, lat]
                sb.append("      <trkpt lat=\"").append(coord[1]).append("\" lon=\"").append(coord[0]).append("\"/>\n");
            }
        }

        sb.append("    </trkseg>\n");
        sb.append("  </trk>\n");
        sb.append(GPX_FOOTER);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String escapeXml(String text) {
        if (text == null) return "Route";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String getIso8601Timestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        return sdf.format(new Date());
    }
}
