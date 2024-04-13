package taack.utils

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@CompileStatic
class DateFormat {
    static Date parse(String pattern, String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
        formatter.parse(dateInString)
    }

    static String format(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
        formatter.format(date)
    }
}
