package com.alinso.myapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {


    public static Date stringToDate(String dateStr, String format) {
        if (dateStr != null && !dateStr.equals("")) {
            Date date = null;
            try {
                date = new SimpleDateFormat(format).parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        } else {
            return null;
        }
    }


    public static String dateToString(Date date, String format) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            String dateString = simpleDateFormat.format(date);
            return dateString;
        }
        else{
            return null;
        }
    }


}
