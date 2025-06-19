package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEnhancer
import org.grails.datastore.gorm.GormEntity
import taack.app.TaackAppRegisterService
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.user.TaackUser

import static taack.render.TaackUiService.tr

@GrailsCompileStatic
@Secured('isAuthenticated()')
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
                    custom '<h2>Access Denied</h2>'
                }
            })
        }
    }

    def readAllUserNotifications(String objectController) {
        Integer option = params.option ? params.int('option') : null
        if (option == 1) {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            currentUser.getUnreadRelatedDataList()?.each {
                if (objectController == 'all' || (TaackAppRegisterService.getTaackLinkClass(it.class.name)?.controller ?: '') == objectController) {
                    currentUser.markRelatedDataAsRead(it)
                }
            }
            taackUiService.ajaxReload()
        } else {
            taackUiService.show(new UiBlockSpecifier().ui {
                modal {
                    show new UiShowSpecifier().ui {
                        String notificationGroupName = objectController ? tr("${objectController}.app", null) : tr('enum.value.OTHER', null)
                        showAction "<h5>Mark all Notifications ${objectController == 'all' ? '' : "of \"${notificationGroupName}\" "}as Read ?</h5>",
                                TaackUserNotificationController.&readAllUserNotifications as MethodClosure,
                                [objectController: objectController, option: 1]
                    }
                }
            })
        }

    }
}