package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import org.grails.datastore.gorm.GormEnhancer
import org.grails.datastore.gorm.GormEntity
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.user.TaackUser

@GrailsCompileStatic
@Secured("isAuthenticated()")
class TaackUserNotificationController implements WebAttributes {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService
    TaackUiEnablerService taackUiEnablerService

    def readUserNotification(String objectController, String objectAction, String objectClass, Long objectId) {
        TaackUser currentUser = springSecurityService.currentUser as TaackUser
        Class theClass = Class.forName(objectClass)
        GormEntity gormEntity = GormEnhancer.findStaticApi(theClass)?.get(objectId) as GormEntity
        currentUser.markRelatedDataAsRead(gormEntity)
        if (taackUiEnablerService.hasAccess(objectController, objectAction, objectId, null)) {
            redirect controller: objectController, action: objectAction, id: objectId, params: [isAjax: true]
        } else {
            taackUiService.show(new UiBlockSpecifier().ui {
                modal {
                    custom "<h2>Access Denied</h2>"
                }
            })
        }
    }
}