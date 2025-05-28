package taack.user

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
interface IUserNotification {

    List<GormEntity> getUnreadRelatedDataList()

    void addToUnreadRelatedDataList(GormEntity gormEntity)

    void markRelatedDataAsRead(GormEntity gormEntity)
}
