package crew

import attachment.DocumentAccess
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.app.TaackApp
import taack.app.TaackAppRegisterService
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.test.Application

import javax.annotation.PostConstruct

@GrailsCompileStatic
class CrewSecurityService {

    static lazyInit = false

    SpringSecurityService springSecurityService

    private securityClosure(Long id, Map p) {
        if (!id && !p) return true
        if (!id) return true
        canEdit(User.read(id))
    }

    @PostConstruct
    void init() {
        TaackUiEnablerService.securityClosure(
                this.&securityClosure,
                CrewController.&editUser as MC,
                CrewController.&saveUser as MC)
        TaackAppRegisterService.register(new TaackApp(CrewController.&index as MC, new String(Application.getResourceAsStream("/crew/crew.svg").readAllBytes())))
        TaackUiService.registerContextualMenuClosure(User, new UiMenuSpecifier().ui {
            menu CrewController.&showUser as MC
            menu CrewController.&editUser as MC
        })
    }

    User authenticatedRolesUser() {
        springSecurityService.currentUser as User
    }

    boolean authenticatedRoles(String... roles) {
        User user = authenticatedRolesUser()
        for (String r : roles) {
            if (user.authorities*.authority.contains(r)) return true
        }
        return false
    }

    boolean canSwitchUser() {
        authenticatedRoles('ROLE_SWITCH_USER', 'ROLE_ADMIN')
    }

    boolean isAdmin() {
        authenticatedRoles('ROLE_ADMIN')
    }

    boolean isManagerOf(User target) {
        User u = authenticatedRolesUser()
        u.id == target.id || u.allManagers*.id.contains(target.id)
    }

    boolean canEdit(User target) {
        admin || canSwitchUser() || isManagerOf(target)
    }

    @Transactional
    DocumentAccess getMainPictureDocumentAccess() {
        DocumentAccess documentAccess = DocumentAccess.findOrSaveWhere(
                isRestrictedToMyManagers: false,
                isInternal: true,
                isRestrictedToMyBusinessUnit: false,
                isRestrictedToMySubsidiary: false,
                isRestrictedToEmbeddingObjects: false
        )
        if (!documentAccess.id) {
            documentAccess.save(flush: true)
            if (documentAccess.hasErrors())
                log.error("${documentAccess.errors}")
        }
        documentAccess
    }

}
