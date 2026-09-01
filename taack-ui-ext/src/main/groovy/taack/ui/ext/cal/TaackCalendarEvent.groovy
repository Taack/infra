package taack.ui.ext.cal

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.dsl.common.Style

import java.time.Duration

@CompileStatic
interface TaackCalendarEvent {
    Date getDateFrom()
    Date getDateTo()
    Style getStyle()
    String getName()
    String getDesc()

    Iterator<Duration> getRepetitions()

    Iterator<GormEntity> getGuest()
}