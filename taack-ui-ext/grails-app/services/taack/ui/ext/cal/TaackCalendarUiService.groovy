package taack.ui.ext.cal

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import taack.ui.ext.TaackCalParams

@GrailsCompileStatic
class TaackCalendarUiService {
    PageRenderer groovyPageRenderer

    String drawCalendar(Iterator<TaackCalendarEvent> events, TaackCalParams taackCalParams) {
        groovyPageRenderer.render(
                template: '/taackCalendar/monthView',
                model: [
                        taackCalendarEvents: events,
                        taackCalendarParams: taackCalParams,
                ])
    }

}