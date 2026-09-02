package calendar

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEntity
import taack.ast.annotation.TaackFieldEnum
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style
import taack.ui.ext.cal.TaackCalendarEvent
import taack.ui.ext.cal.TaackCalendarParams
import taack.ui.ext.cal.TaackCalendarUiService
import taack.ui.test.RootController

import java.time.Duration

@TaackFieldEnum
@GrailsCompileStatic
class CalendarEvent implements Validateable {
    String title
    String body
    Date fromDate
    Date toDate

    static constraints = {
        body nullable: true
        toDate validator: {Date d, CalendarEvent c ->
            if (d <= fromDate) return 'toDate must be greater than fromDate'
        }
    }

    @Override
    String toString() {
        "$fromDate - $toDate: $title"
    }
}

class TaackCalendarEventAdapter implements TaackCalendarEvent {

    final Date dateFrom
    final Date dateTo
    final String name
    final String desc

    TaackCalendarEventAdapter(CalendarEvent calendarEvent) {
        name = calendarEvent.title
        desc = calendarEvent.body
        dateFrom = calendarEvent.fromDate
        dateTo = calendarEvent.toDate
    }

    @Override
    Style getStyle() {
        return null
    }

    @Override
    MethodClosure getAction() {
        return null
    }

    @Override
    Long getId() {
        return null
    }

    @Override
    String toString() {
        return "TaackCalendarEventAdapter{" +
                "dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

@GrailsCompileStatic
@Secured(['permitAll'])
class CalendarController {

    TaackUiService taackUiService
    List<CalendarEvent> calendarEvents = []
    TaackCalendarUiService taackCalendarUiService

    @PostConstruct
    void init() {

        Calendar rightNow = Calendar.getInstance()
        rightNow.set(Calendar.HOUR_OF_DAY, 8)
        rightNow.set(Calendar.MINUTE, 0)
        rightNow.set(Calendar.SECOND, 0)
        rightNow.add(Calendar.DAY_OF_WEEK, -1)
        Date yesterday = rightNow.time
        rightNow.add(Calendar.DAY_OF_WEEK, 1)
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

        calendarEvents << new CalendarEvent(title: 'Test0', fromDate: yesterday, toDate: today10)
        calendarEvents << new CalendarEvent(title: 'Test3', fromDate: today8, toDate: tomorrow930)
        calendarEvents << new CalendarEvent(title: 'Test1', fromDate: today8, toDate: today10)
        calendarEvents << new CalendarEvent(title: 'Test2', fromDate: tomorrow9, toDate: tomorrow13)
        calendarEvents << new CalendarEvent(title: 'Test10', fromDate: yesterday, toDate: tomorrow930)

        println calendarEvents
    }

    def createEvent(CalendarEvent event) {
        event ?= new CalendarEvent()

        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                form(new UiFormSpecifier().ui(event) {
                    field event.title_
                    field event.fromDate_
                    field event.toDate_
                    field event.body_
                    formAction(this.&saveEvent as MC)
                })
            }
        })
    }

    def saveEvent() {

    }

    def fromCustom(TaackCalendarParams calendarParams) {
        taackUiService.show taackCalendarUiService.calendarBlock(CalendarController.&fromCustom as MC, calendarEvents.collect {
            new TaackCalendarEvent() {

                @Override
                Date getDateFrom() {
                    return it.fromDate
                }

                @Override
                Date getDateTo() {
                    return it.toDate
                }

                @Override
                Style getStyle() {
                    return null
                }

                @Override
                String getName() {
                    return it.title
                }

                @Override
                MethodClosure getAction() {
                    return null
                }

                @Override
                Long getId() {
                    return null
                }
            }
        }.iterator() as Iterator<TaackCalendarEvent>, calendarParams), RootController.buildMenu()
    }

    def fromUiTable(TaackCalendarParams calendarParams) {
        UiTableSpecifier t = taackCalendarUiService.calendarUiTable(calendarEvents.collect {
            new TaackCalendarEventAdapter(it)
        }.iterator() as Iterator<TaackCalendarEvent>, calendarParams)
        taackUiService.show new UiBlockSpecifier().ui {
            table t
        }, RootController.buildMenu()
    }
}
