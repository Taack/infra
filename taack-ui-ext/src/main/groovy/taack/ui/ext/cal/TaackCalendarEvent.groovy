package taack.ui.ext.cal

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.dsl.common.Style

import java.time.Duration

@CompileStatic
interface TaackCalendarEvent {
    Date getDateFrom()
    Date getDateTo()
    Style getStyle()
    String getName()

    MethodClosure getAction()
    Long getId()
}