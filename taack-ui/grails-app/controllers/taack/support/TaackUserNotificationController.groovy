package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEnhancer
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackGormClass
import taack.domain.TaackGormClassRegisterService
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle
import taack.user.TaackUser

import java.text.SimpleDateFormat

import static taack.render.TaackUiService.tr

@GrailsCompileStatic
@Secured("isAuthenticated()")
class TaackUserNotificationController implements WebAttributes {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService
    TaackUiEnablerService taackUiEnablerService

    def showUserNotifications() {
        taackUiService.show(new UiBlockSpecifier().ui {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            Closure c = { boolean unread ->
                Map<GormEntity, Date> notifications = currentUser.getNotificationRelatedDataList(unread)
                table new UiTableSpecifier().ui({
                    header {
                        if (unread) {
                            columnSelect "selectedNotifications", {
                                columnSelectButton "Mark as read", TaackUserNotificationController.&markSelectedNotificationsAsRead as MethodClosure
                            }
                        }
                        label "Date"
                        label "From"
                        label "Type"
                        label "Content"
                    }
                    notifications?.sort { -it.value.time }?.each { Map.Entry<GormEntity, Date> entry ->
                        row {
                            GormEntity object = entry.key
                            if (unread) {
                                rowSelect "${object.ident().toString()}&${object.class.name}"
                            }
                            rowField new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.value)
                            TaackGormClass c = TaackGormClassRegisterService.getTaackGormClass(object.class.name)
                            rowField c?.notification?.getTitleClosure()?.call(object.ident())?.toString()
                            rowField c?.typeLabel?.call(object.ident())?.toString()
                            rowColumn {
                                rowAction ActionIcon.SHOW * IconStyle.SCALE_DOWN, TaackUserNotificationController.&readUserNotification as MethodClosure,
                                        [objectController: c?.showController, objectAction: c?.showAction, objectClass: object.class.name, objectId: object.ident()]
                                rowField(c?.showLabel?.call(object.ident())?.toString() ?: object.toString())
                            }
                        }
                    }
                }), {
                    label "${unread ? "Unread" : "Read"} Notifications"
                }
            }
            c(true)
            c(false)
        }, new UiMenuSpecifier().ui {})
    }

    def markSelectedNotificationsAsRead(String selectedNotifications) {
        TaackUser currentUser = springSecurityService.currentUser as TaackUser
        selectedNotifications.split(",").each {
            List<String> objectInfo = it.split("&").toList()
            GormEntity gormEntity = GormEnhancer.findStaticApi(Class.forName(objectInfo[1]))?.get(objectInfo[0].toLong()) as GormEntity
            currentUser.markRelatedDataAsRead(gormEntity)
        }
        taackUiService.ajaxReload()
    }

    def readUserNotification(String objectClass, Long objectId) {
        TaackUser currentUser = springSecurityService.currentUser as TaackUser
        Class theClass = Class.forName(objectClass)
        GormEntity gormEntity = GormEnhancer.findStaticApi(theClass)?.get(objectId) as GormEntity
        currentUser.markRelatedDataAsRead(gormEntity)

        TaackGormClass c = TaackGormClassRegisterService.getTaackGormClass(objectClass)
        String redirectController = c?.showController
        String redirectAction = c?.showAction
        if (taackUiEnablerService.hasAccess(redirectController, redirectAction, objectId, null)) {
            redirect controller: redirectController, action: redirectAction, id: objectId, params: [isAjax: true]
        } else {
            taackUiService.show(new UiBlockSpecifier().ui {
                modal {
                    custom "<h2>Access Denied</h2>"
                }
            })
        }
    }

    def readAllUserNotifications(String title) {
        Integer option = params.option ? params.int('option') : null
        if (option == 1) {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            currentUser.getNotificationRelatedDataList(true)?.keySet()?.each {
                if (title == "all" || (TaackGormClassRegisterService.getTaackGormClass(it.class.name)?.notification?.getTitleClosure()?.call(it.ident()) ?: "other") == title) {
                    currentUser.markRelatedDataAsRead(it)
                }
            }
            taackUiService.ajaxReload()
        } else {
            taackUiService.show(new UiBlockSpecifier().ui {
                modal {
                    show new UiShowSpecifier().ui {
                        showAction "<h5>Mark all Notifications ${title == "all" ? "" : "of \"${title ?: tr("enum.value.OTHER", null)}\" "}as Read ?</h5>",
                                TaackUserNotificationController.&readAllUserNotifications as MethodClosure,
                                [title: title, option: 1]
                    }
                }
            })
        }

    }
}