package org.desp.wanderingMarket.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateUtil {
    public static String getCurrentTime() {
        Date now = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = "-";
        dateTime = dateFormatter.format(now.getTime());
        return dateTime;
    }

    public static String getCurrentDate() {
        LocalDate today = LocalDate.now();
        return today.toString(); // "yyyy-MM-dd" 형식
    }
}
