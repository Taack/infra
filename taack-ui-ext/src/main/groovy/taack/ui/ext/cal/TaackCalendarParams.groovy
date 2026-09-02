package taack.ui.ext.cal

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import taack.ast.annotation.TaackFieldEnum

@TaackFieldEnum
@GrailsCompileStatic
class TaackCalendarParams implements Validateable {

    TaackCalendarParams() {
        Calendar now = Calendar.getInstance()
        now.setFirstDayOfWeek(Calendar.MONDAY)
        year = now.get(Calendar.YEAR)
        month = now.get(Calendar.MONTH)
        weekOfYear = now.get(Calendar.WEEK_OF_YEAR)
    }

    Integer year
    Integer month
    Integer weekOfYear

    TaackCalendarParams prevMonth() {
        if (month == 0) return new TaackCalendarParams(year: year - 1, month: 11)
        new TaackCalendarParams(year: year, month: month - 1)
    }

    TaackCalendarParams nextMonth() {
        if (month == 11) return new TaackCalendarParams(year: year + 1, month: 0)
        new TaackCalendarParams(year: year, month: month + 1)
    }

    @Override
    String toString() {
        return "TaackCalParams{" +
                "year=" + year +
                ", month=" + month +
                ", weekOfYear=" + weekOfYear +
                '}'
    }

}
