package calendar

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import jakarta.annotation.PostConstruct
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style


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

@GrailsCompileStatic
@Secured(['permitAll'])
class CalendarController {

    TaackUiService taackUiService
    List<CalendarEvent> calendarEvents = []

    static final Map<Integer, String> dayNames = [1: 'Monday', 2: 'Tuesday', 3: 'Wednesday', 4: 'Thursday', 5: 'Friday', 6: 'Saturday', 7: 'Sunday']

    @PostConstruct
    void init() {

        Calendar rightNow = Calendar.getInstance()
        rightNow.set(Calendar.HOUR_OF_DAY, 8)
        rightNow.set(Calendar.MINUTE, 0)
        rightNow.set(Calendar.SECOND, 0)
        Date today8 = rightNow.time
        rightNow.set(Calendar.HOUR, 10)
        Date today10 = rightNow.time
        rightNow.add(Calendar.DAY_OF_WEEK, 1)
        rightNow.add(Calendar.HOUR, -1)
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

    def index() {
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
                                            rowField(dayNames[dowIt] + " (${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)})")
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
        }
    }
}
