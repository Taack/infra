package taack.ui.ext

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import groovy.transform.CompileStatic
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.ast.annotation.TaackFieldEnum
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style

@TaackFieldEnum
@GrailsCompileStatic
class TaackCalEvent implements Validateable {
    String title
    String body
    Date fromDate
    Date toDate

    static constraints = {
        body nullable: true
        toDate validator: {Date d, TaackCalEvent c ->
            if (d <= fromDate) return 'toDate must be greater than fromDate'
        }
    }

    @Override
    String toString() {
        "$fromDate - $toDate: $title"
    }
}

@CompileStatic
enum TaackCalDow {
    MON, TUE, WED, THU, FRI, SAT, SUN
}

@CompileStatic
enum TaackCalMonth {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
}

@TaackFieldEnum
@GrailsCompileStatic
class TaackCalParams implements Validateable {

    TaackCalParams() {
        Calendar now = Calendar.getInstance()
        now.setFirstDayOfWeek(Calendar.MONDAY)
        year = now.get(Calendar.YEAR)
        month = TaackCalMonth.values()[now.get(Calendar.MONTH)]
        weekOfYear = now.get(Calendar.WEEK_OF_YEAR)
    }

    Integer year
    TaackCalMonth month
    Integer weekOfYear

    TaackCalParams computePrevMonth() {
        if (month.ordinal() == 0) return new TaackCalParams(year: year - 1, month: TaackCalMonth.values()[11])
        new TaackCalParams(year: year, month: TaackCalMonth.values()[month.ordinal() - 1])
    }

    TaackCalParams computeNextMonth() {
        if (month.ordinal() == 11) return new TaackCalParams(year: year + 1, month: TaackCalMonth.values()[0])
        new TaackCalParams(year: year, month: TaackCalMonth.values()[month.ordinal() + 1])
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

@GrailsCompileStatic
@Secured(['permitAll'])
class TaackCalController {

    TaackUiService taackUiService
    List<TaackCalEvent> TaackCalEvents = []
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

        TaackCalEvents << new TaackCalEvent(title: 'Test1', fromDate: today8, toDate: today10)
        TaackCalEvents << new TaackCalEvent(title: 'Test2', fromDate: tomorrow9, toDate: tomorrow13)
        TaackCalEvents << new TaackCalEvent(title: 'Test3', fromDate: today8, toDate: tomorrow930)

        println TaackCalEvents
    }

    def createEvent(TaackCalEvent event) {
        event ?= new TaackCalEvent()

        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                form(new UiFormSpecifier().ui(event) {
                    field event.title_
                    field event.fromDate_
                    field event.toDate_
                    field event.body_
                    formAction(this.&saveEvent as MethodClosure)
                })
            }
        })
    }

    def saveEvent() {

    }

    def fromCustom(TaackCalParams TaackCalParams) {
        TaackCalParams ?= new TaackCalParams()
        UiBlockSpecifier TaackCal = new UiBlockSpecifier().ui {
            custom(groovyPageRenderer.render(
                    template: '/TaackCal/monthView',
                    model: [
                            events        : TaackCalEvents,
                            TaackCalParams: TaackCalParams,
                    ]), null) {
                label("${TaackCalParams.month} ${TaackCalParams.year}")
                menu 'Prev', TaackCalController.&fromCustom as MC, TaackCalParams.computePrevMonth()
                menu 'Today', TaackCalController.&fromCustom as MC, new TaackCalParams()
                menu 'Next', TaackCalController.&fromCustom as MC, TaackCalParams.computeNextMonth()
            }
        }

//        taackUiService.show(TaackCal, RootController.buildMenu())
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
                TaackCalEvent[] sortedEvent = TaackCalEvents.sort { it1, it2 -> it1.fromDate <=> it2.fromDate ?: it1.toDate <=> it2.toDate } as TaackCalEvent[]
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
                                        for (TaackCalEvent eventIt : sortedEvent) {
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
        } //, RootController.buildMenu()
    }
}
