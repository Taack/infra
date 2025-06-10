package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@CompileStatic
enum DiagramXLabelDateFormat {
    YEAR(Calendar.YEAR, Calendar.MONTH, "yyyy", "yyyy"),
    MONTH(Calendar.MONTH, Calendar.DAY_OF_MONTH, "yyyy-MM", "yyyy-MM"),
    DAY(Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, "yyyy-MM-dd", "yyyy-MM-dd"),
    HOUR(Calendar.HOUR_OF_DAY, Calendar.MINUTE, "yyyy-MM-dd HH", "MM-dd HH")

    DiagramXLabelDateFormat(int unit, int subUnit, String dateFormat, String formatToDisplay) {
        this.unit = unit
        this.subUnit = subUnit
        this.dateFormat = dateFormat
        this.formatToDisplay = formatToDisplay
    }

    final int unit
    final int subUnit
    final String dateFormat
    final String formatToDisplay

    String format(Date date) {
        return new SimpleDateFormat(formatToDisplay).format(date) + (this == HOUR ? "h" : "")
    }
}