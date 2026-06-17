package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@CompileStatic
enum DiagramXLabelDateFormat {
    YEAR(Calendar.YEAR, Calendar.MONTH, 'yyyy', 'yyyy', 'yyyy-MM'),
    MONTH(Calendar.MONTH, Calendar.DAY_OF_MONTH, 'yyyy-MM', 'yyyy-MM', 'yyyy-MM-dd'),
    DAY(Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, 'yyyy-MM-dd', 'yyyy-MM-dd', 'yyyy-MM-dd HH\'h\'mm'),
    HOUR(Calendar.HOUR_OF_DAY, Calendar.MINUTE, 'yyyy-MM-dd HH', 'MM-dd HH\'h\'', 'HH\'h\'mm')

    DiagramXLabelDateFormat(int unit, int subUnit, String dateFormat, String formatToDisplay, String detailFormatToDisplay) {
        this.unit = unit
        this.subUnit = subUnit
        this.dateFormat = dateFormat
        this.formatToDisplay = formatToDisplay
        this.detailFormatToDisplay = detailFormatToDisplay
    }

    final int unit
    final int subUnit
    final String dateFormat
    final String formatToDisplay
    final String detailFormatToDisplay

    String format(Date date) {
        return new SimpleDateFormat(formatToDisplay).format(date)
    }

    String detailFormat(Date date) {
        return new SimpleDateFormat(detailFormatToDisplay).format(date)
    }
}