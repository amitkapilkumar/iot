package com.consumer.iot.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AppUtil {
    private AppUtil() { }

    public static final String EMPTY = "";
    public static final String SPLIT_TOKEN = ",";
    public static final String CYCLE_PLUS_TRACKER = "CyclePlusTracker";
    public static final String GENERAL_TRACKER = "GeneralTracker";
    public static final String ACTIVE = "Active";
    public static final String IN_ACTIVE = "Inactive";
    public static final String DESCRIPTION_LOCATION_UNIDENTIFIED = "SUCCESS: Location not available: Please turn off airplane mode";
    public static final String DESCRIPTION_LOCATION_IDENTIFIED = "SUCCESS: Location identified";

    private static final String format = "dd/MM/yyyy HH:mm:ss";

    public static boolean isFileExists(String filepath) {
        return new File(filepath).exists();
    }

    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static String getName(String id) {
        if(id.startsWith("WG")) {
            return CYCLE_PLUS_TRACKER;
        }
        return GENERAL_TRACKER;
    }

}
