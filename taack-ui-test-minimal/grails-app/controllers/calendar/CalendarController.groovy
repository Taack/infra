package calendar

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import groovy.transform.CompileStatic
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.ast.annotation.TaackFieldEnum
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style
import taack.ui.test.RootController

@GrailsCompileStatic
class CalendarEvent implements Validateable {
    String title
    String body
    Date fromDate
    Date toDate

    static constraints = {
        body nullable: true
    }

    @Override
    String toString() {
        "$fromDate - $toDate: $title"
    }
}

@CompileStatic
enum CalendarDow {
    MON, TUE, WED, THU, FRI, SAT, SUN
}

@CompileStatic
enum CalendarMonth {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
}

@TaackFieldEnum
@GrailsCompileStatic
class CalendarParams implements Validateable {

    CalendarParams() {
        Calendar now = Calendar.getInstance()
        now.setFirstDayOfWeek(Calendar.MONDAY)
        year = now.get(Calendar.YEAR)
        month = CalendarMonth.values()[now.get(Calendar.MONTH)]
        weekOfYear = now.get(Calendar.WEEK_OF_YEAR)
    }

    Integer year
    CalendarMonth month
    Integer weekOfYear

    CalendarParams computePrevMonth() {
        if (month.ordinal() == 0) return new CalendarParams(year: year - 1, month: CalendarMonth.values()[11])
        new CalendarParams(year: year, month: CalendarMonth.values()[month.ordinal() - 1])
    }

    CalendarParams computeNextMonth() {
        if (month.ordinal() == 11) return new CalendarParams(year: year + 1, month: CalendarMonth.values()[0])
        new CalendarParams(year: year, month: CalendarMonth.values()[month.ordinal() + 1])
    }

}

@GrailsCompileStatic
@Secured(['permitAll'])
class CalendarController {

    TaackUiService taackUiService
    List<CalendarEvent> calendarEvents = []
    PageRenderer groovyPageRenderer

    static final Map<Integer, String> dayNames = [1: 'Monday', 2: 'Tuesday', 3: 'Wednesday', 4: 'Thursday', 5: 'Friday', 6: 'Saturday', 7: 'Sunday']

    @PostConstruct
    void init() {

        Calendar rightNow = Calendar.getInstance()
        rightNow.set(Calendar.HOUR_OF_DAY, 8)
        rightNow.set(Calendar.MINUTE, 0)
        rightNow.set(Calendar.SECOND, 0)
        Date today8 = rightNow.time
        rightNow.set(Calendar.HOUR_OF_DAY, 10)
        Date today10 = rightNow.time
        rightNow.add(Calendar.DAY_OF_WEEK, 1)
        rightNow.set(Calendar.HOUR_OF_DAY, 9)
        Date tomorrow9 = rightNow.time
        rightNow.add(Calendar.MINUTE, 30)
        Date tomorrow930 = rightNow.time
        rightNow.set(Calendar.HOUR_OF_DAY, 13)
        rightNow.set(Calendar.MINUTE, 0)
        Date tomorrow13 = rightNow.time

        calendarEvents << new CalendarEvent(title: 'Test1', fromDate: today8, toDate: today10)
        calendarEvents << new CalendarEvent(title: 'Test2', fromDate: tomorrow9, toDate: tomorrow13)
        calendarEvents << new CalendarEvent(title: 'Test3', fromDate: today8, toDate: tomorrow930)

        println calendarEvents
    }

    def fromCustom(CalendarParams calendarParams) {
        calendarParams ?= new CalendarParams()
        UiBlockSpecifier calendar = new UiBlockSpecifier().ui {
            custom(groovyPageRenderer.render(
                    template: '/calendar/monthView2',
                    model: [
                            events        : calendarEvents,
                            calendarParams: calendarParams,
                    ]), null) {
                label("${calendarParams.month} ${calendarParams.year}")
                menu 'Prev', CalendarController.&fromCustom as MC, calendarParams.computePrevMonth()
                menu 'Today', CalendarController.&fromCustom as MC, new CalendarParams()
                menu 'Next', CalendarController.&fromCustom as MC, calendarParams.computeNextMonth()

            }
        }

        taackUiService.show(calendar, RootController.buildMenu())
    }

    def fromUiTable() {
        taackUiService.show new UiBlockSpecifier().ui {
            table new UiTableSpecifier().ui {
                header {
                    column {
                        label 'Year/WoY/Date From'
                        label 'Date To'
                    }
                    label 'Title'
                }
                CalendarEvent[] sortedEvent = calendarEvents.sort { it1, it2 -> it1.fromDate <=> it2.fromDate ?: it1.toDate <=> it2.toDate } as CalendarEvent[]
                Date dateStart = sortedEvent.first().fromDate
                Date dateEnd = sortedEvent.last().toDate

                println "dateStart: $dateStart -> dateEnd: $dateEnd"

                Calendar c = Calendar.getInstance()
                c.setFirstDayOfWeek(1)
                c.setTime(dateStart)
                int y = c.get(Calendar.YEAR)
                int woy = c.get(Calendar.WEEK_OF_YEAR)
                int dow = c.get(Calendar.DAY_OF_WEEK)
//                if (dow < c.firstDayOfWeek) woy += 1
                println "y: $y, woy: $woy, dow: $dow, firstDayOfWeek: ${c.firstDayOfWeek}"
                c.setTime(dateEnd)
                int yEnd = c.get(Calendar.YEAR)
                int woyEnd = c.get(Calendar.WEEK_OF_YEAR)
                int dowEnd = c.get(Calendar.DAY_OF_WEEK)
//                if (dowEnd < c.firstDayOfWeek) woyEnd += 1
                println "yEnd: $yEnd, woyEnd: $woyEnd, dowEnd: $dowEnd, firstDayOfWeek: ${c.firstDayOfWeek}"

                for (int yIt : y..yEnd) {
                    c.set(Calendar.YEAR, yIt)

                    rowIndent(true) {
//                        rowTree(true) {
//                            rowField yIt, Style.RED + Style.ALIGN_CENTER
//                        }
                        for (int woyIt : woy..woyEnd) {
                            rowTree(true) {
                                rowColumn(2) {
                                    rowField "$yIt / $woyIt (${c.get(Calendar.MONTH)})", Style.BLUE + Style.BOLD
                                }
                            }
                            c.set(Calendar.WEEK_OF_YEAR, woyIt)
                            rowIndent(true) {

                                for (int dowIt : 1..7) {

                                    rowTree(true) {
                                        rowColumn(2) {
                                            rowField(this.dayNames[dowIt] + " (${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)})")
                                        }
                                    }
                                    c.set(Calendar.DAY_OF_WEEK, dowIt)
                                    c.set(Calendar.HOUR_OF_DAY, 0)
                                    c.set(Calendar.MINUTE, 0)
                                    c.set(Calendar.SECOND, 0)
                                    Date dayStart = c.time
                                    c.add(Calendar.HOUR, 24)
                                    Date dayEnd = c.time
                                    rowIndent(true) {
                                        for (CalendarEvent eventIt : sortedEvent) {
                                            if (eventIt.fromDate >= dayStart && eventIt.fromDate <= dayEnd || eventIt.toDate >= dayStart && eventIt.toDate <= dayEnd)
                                                rowTree(false) {
                                                    rowColumn {
                                                        Calendar displayDate = Calendar.getInstance()
                                                        displayDate.setTime(eventIt.fromDate)
                                                        int m1 = displayDate.get(Calendar.MINUTE)
                                                        int mo1 = displayDate.get(Calendar.MONTH)
                                                        int h1 = displayDate.get(Calendar.HOUR_OF_DAY)
                                                        int d1 = displayDate.get(Calendar.DAY_OF_YEAR)
                                                        int dm1 = displayDate.get(Calendar.DAY_OF_MONTH)
                                                        int y1 = displayDate.get(Calendar.YEAR)
                                                        displayDate.setTime(eventIt.toDate)
                                                        int m2 = displayDate.get(Calendar.MINUTE)
                                                        int mo2 = displayDate.get(Calendar.MONTH)
                                                        int h2 = displayDate.get(Calendar.HOUR_OF_DAY)
                                                        int d2 = displayDate.get(Calendar.DAY_OF_YEAR)
                                                        int dm2 = displayDate.get(Calendar.DAY_OF_MONTH)
                                                        int y2 = displayDate.get(Calendar.YEAR)
                                                        if (y2 == y1 && d2 == d1) {
                                                            rowField "$h1:$m1 -> $h2:$m2"
                                                        } else {
                                                            rowField "${String.format('%02d', mo1)}/${String.format('%02d', dm1)} ${String.format('%02d', h1)}:${String.format('%02d', m1)} -> ${String.format('%02d', mo2)}/${String.format('%02d', dm2)} ${String.format('%02d', h2)}:${String.format('%02d', m2)}"
                                                        }
                                                    }
                                                    rowField eventIt.title, Style.BOLD
                                                }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }, RootController.buildMenu()
    }
}
