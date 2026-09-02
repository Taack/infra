package taack.ui.ext.cal

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style

import java.text.DateFormatSymbols

@GrailsCompileStatic
class TaackCalendarUiService {
    PageRenderer groovyPageRenderer

    String drawCalendar(Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        groovyPageRenderer.render(
                template: '/taackCalendar/monthView',
                model: [
                        taackCalendarEvents: events,
                        taackCalendarParams: taackCalParams,
                ])
    }

    UiBlockSpecifier calendarBlock(Locale locale, MethodClosure currentMethod, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        new UiBlockSpecifier().ui {
            custom(drawCalendar(events, taackCalParams), null) {
                String monthName = new DateFormatSymbols(locale).getMonths()[taackCalParams.month]
                label("${monthName} ${taackCalParams.year}")
                menu 'Prev', currentMethod, taackCalParams.prevMonth()
                menu 'Today', currentMethod, new TaackCalendarParams()
                menu 'Next', currentMethod, taackCalParams.nextMonth()
            }
        }
    }

    UiTableSpecifier calendarUiTable(Locale locale, MethodClosure currentMethod, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        new UiTableSpecifier().ui {
            header {
                column {
                    label 'Year/WoY/Date From'
                    label 'Date To'
                }
                label 'Title'
            }
            Iterator<TaackCalendarEvent> sortedEvent = events.sort { it1, it2 -> it1.dateFrom <=> it2.dateFrom ?: it1.dateTo <=> it2.dateTo }
            // Date dateStart = sortedEvent.first().fromDate
            // Date dateEnd = sortedEvent.last().toDate

            //println "dateStart: $dateStart -> dateEnd: $dateEnd"

            Calendar c = Calendar.getInstance()
            c.setFirstDayOfWeek(1)
            //c.setTime(dateStart)
            //int y = c.get(Calendar.YEAR)
            //int woy = c.get(Calendar.WEEK_OF_YEAR)
            //int dow = c.get(Calendar.DAY_OF_WEEK)
//                if (dow < c.firstDayOfWeek) woy += 1
            //println "y: $y, woy: $woy, dow: $dow, firstDayOfWeek: ${c.firstDayOfWeek}"
            //c.setTime(dateEnd)
            //int yEnd = c.get(Calendar.YEAR)
            //int woyEnd = c.get(Calendar.WEEK_OF_YEAR)
            //int dowEnd = c.get(Calendar.DAY_OF_WEEK)
//                if (dowEnd < c.firstDayOfWeek) woyEnd += 1
            //println "yEnd: $yEnd, woyEnd: $woyEnd, dowEnd: $dowEnd, firstDayOfWeek: ${c.firstDayOfWeek}"

            int prevWoy = 0
            TaackCalendarEvent breakingEvent = null
            while (sortedEvent.hasNext()) {
                TaackCalendarEvent e = breakingEvent ?: sortedEvent.next()
                c.setTime(e.dateFrom)
                int woy = c.get(Calendar.WEEK_OF_YEAR)
                int year = c.get(Calendar.YEAR)
                rowIndent(true) {
//                        rowTree(true) {
//                            rowField yIt, Style.RED + Style.ALIGN_CENTER
//                        }
                    for (int woyIt : prevWoy..woy) {
                        rowTree(true) {
                            rowColumn(2) {
                                rowField "$year / $woyIt (${c.get(Calendar.MONTH)})", Style.BLUE + Style.BOLD
                            }
                        }
                        rowIndent(true) {
                            for (int dowIt : 1..7) {

                                rowTree(true) {
                                    rowColumn(2) {
                                        String dayName = new DateFormatSymbols(locale).getWeekdays()[dowIt]
                                        String monthName = new DateFormatSymbols(locale).getMonths()[c.get(Calendar.MONTH)]
                                        rowField(dayName + " (${monthName}/${c.get(Calendar.DAY_OF_MONTH)})")
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
                                    List<TaackCalendarEvent> eventsOfTheDay = [e]
                                    while (sortedEvent.hasNext()) {
                                        breakingEvent = sortedEvent.next()
                                        if (breakingEvent.dateFrom.before(dayEnd))
                                            eventsOfTheDay.add(breakingEvent)
                                        else break
                                    }
                                    for (TaackCalendarEvent eventIt : eventsOfTheDay) {
                                        rowTree(false) {
                                            rowColumn {
                                                Calendar displayDate = Calendar.getInstance()
                                                displayDate.setTime(eventIt.dateFrom)
                                                int m1 = displayDate.get(Calendar.MINUTE)
                                                int mo1 = displayDate.get(Calendar.MONTH)
                                                int h1 = displayDate.get(Calendar.HOUR_OF_DAY)
                                                int d1 = displayDate.get(Calendar.DAY_OF_YEAR)
                                                int dm1 = displayDate.get(Calendar.DAY_OF_MONTH)
                                                int y1 = displayDate.get(Calendar.YEAR)
                                                displayDate.setTime(eventIt.dateTo)
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
                                            rowField eventIt.getName(), Style.BOLD
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