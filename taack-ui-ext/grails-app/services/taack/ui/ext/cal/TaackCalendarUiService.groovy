package taack.ui.ext.cal

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import org.codehaus.groovy.runtime.MethodClosure
import org.springframework.context.i18n.LocaleContextHolder
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style

import java.text.DateFormatSymbols

@GrailsCompileStatic
class TaackCalendarUiService {
    PageRenderer groovyPageRenderer

    String drawCalendar(Locale locale, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {

        List<TaackCalendarEvent> eventList = events.toList()

        String c = groovyPageRenderer.render(
                template: '/taackCalendar/monthView',
                model: [
                        dayNames           : new DateFormatSymbols(locale).getWeekdays(),
                        taackCalendarEvents: eventList,
                        taackCalendarParams: taackCalParams,
                ])
        c
    }

    UiBlockSpecifier calendarBlock(Locale locale, MethodClosure currentMethod, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        new UiBlockSpecifier().ui {
            custom(drawCalendar(locale, events, taackCalParams), null) {
                String monthName = new DateFormatSymbols(locale).getMonths()[taackCalParams.month]
                label("${monthName} ${taackCalParams.year}")
                menu 'Prev', currentMethod, taackCalParams.prevMonth()
                menu 'Today', currentMethod, new TaackCalendarParams()
                menu 'Next', currentMethod, taackCalParams.nextMonth()
            }
        }
    }

    UiBlockSpecifier calendarBlock(MethodClosure currentMethod, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        calendarBlock(LocaleContextHolder.getLocale(), currentMethod, events, taackCalParams)
    }

    UiTableSpecifier calendarUiTable(Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        calendarUiTable(LocaleContextHolder.getLocale(), events, taackCalParams)
    }

    UiTableSpecifier calendarUiTable(Locale locale, Iterator<TaackCalendarEvent> events, TaackCalendarParams taackCalParams) {
        new UiTableSpecifier().ui {
            header {
                column {
                    label 'Year/WoY/Date From'
                    label 'Date To'
                }
                label 'Title'
            }
            List<TaackCalendarEvent> sortedEventList = events.sort { it1, it2 -> it1.dateFrom <=> it2.dateFrom ?: it1.dateTo <=> it2.dateTo }.toList()
            Calendar c = Calendar.getInstance()
            for (int index = 0; index < sortedEventList.size(); index++) {
                TaackCalendarEvent e = sortedEventList[index]
                c.setTime(e.dateFrom)
                int woy = c.get(Calendar.WEEK_OF_YEAR)
                int year = c.get(Calendar.YEAR)
                rowIndent(true) {
                    rowTree(true) {
                        rowColumn(2) {
                            rowField "$year / $woy (${c.get(Calendar.MONTH)})", Style.BLUE + Style.BOLD
                        }
                    }
                    rowIndent(true) {
                        for (int dowIt : 1..7) {
                            int someDow = c.get(Calendar.DAY_OF_WEEK)
                            int someDom = c.get(Calendar.DAY_OF_MONTH)
                            c.set(Calendar.HOUR_OF_DAY, 0)
                            c.set(Calendar.MINUTE, 0)
                            c.set(Calendar.SECOND, 0)
                            Date dayStart = c.time
                            c.add(Calendar.HOUR, 24)
                            Date dayEnd = c.time
                            List<TaackCalendarEvent> eventsOfTheDay = []
                            for (int index2 = index; index2 < sortedEventList.size(); index2++) {
                                TaackCalendarEvent e2 = sortedEventList[index2]
                                if (!(e2.dateFrom >= dayEnd || e2.dateTo <= dayStart)) eventsOfTheDay.add(e2)
                                if (e2.dateFrom > dayEnd) break
                                if (e2.dateTo < dayStart) index = index2
                            }
                            if (eventsOfTheDay.size() > 0) {
                                rowTree(true) {
                                    rowColumn(2) {
                                        String dayName = new DateFormatSymbols(locale).getWeekdays()[someDow]
                                        String monthName = new DateFormatSymbols(locale).getMonths()[c.get(Calendar.MONTH)]
                                        rowField(dayName + " (${monthName}/${someDom})")
                                    }
                                }
                                rowIndent(true) {
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
                                            rowColumn {
                                                rowAction eventIt.getName(), eventIt.action, eventIt.id// , Style.BOLD
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