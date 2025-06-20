package taack.user

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
interface IUserNotification {

    Map<GormEntity, Date> getNotificationRelatedDataList(Boolean unread)

    void addToUnreadRelatedDataList(GormEntity gormEntity)

    void markRelatedDataAsRead(GormEntity gormEntity)
}
