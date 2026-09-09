package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import jakarta.annotation.PostConstruct
import org.grails.datastore.gorm.GormEntity
import taack.user.IUserCreated
import taack.user.IUserUpdated
import taack.user.TaackUser

@GrailsCompileStatic
class RegisterToTaackSaveService {

    static lazyInit = false

    SpringSecurityService springSecurityService

    @PostConstruct
    void init() {
        log.info 'init'
        TaackSaveService.registerFieldCustomSavingClosure('userCreated', { GormEntity gormEntity, Map params ->
            try {
                TaackUser currentUser = springSecurityService.currentUser as TaackUser
                if (gormEntity.hasChanged()) {
                    if (gormEntity.hasProperty('userCreated') && gormEntity['userCreated'] == null) {
                        gormEntity['userCreated'] = currentUser
                    }
                }
            } catch (ignored) {
            }
        })
        TaackSaveService.registerFieldCustomSavingClosure('userUpdated', { GormEntity gormEntity, Map params ->
            try {
                TaackUser currentUser = springSecurityService.currentUser as TaackUser
                if (gormEntity.hasChanged()) {
                    if (gormEntity.hasProperty('userUpdated')) {
                        gormEntity['userUpdated'] = currentUser
                    }
                }
            } catch (ignored) {
            }
        })
        TaackSaveService.registerFieldCustomSavingClosure('userLastUpdated', { GormEntity gormEntity, Map params ->
            try {
                if (gormEntity.hasChanged()) {
                    TaackUser currentUser = springSecurityService.currentUser as TaackUser
                    if (gormEntity.hasProperty('userLastUpdated')) {
                        gormEntity['userLastUpdated'] = currentUser
                    }
                }
            } catch (ignored) {
            }
        })
        TaackSaveService.registerInterfaceCustomSavingClosure(IUserCreated, { GormEntity gormEntity, Map params ->
            try {
                if (gormEntity.hasChanged()) {
                    TaackUser currentUser = springSecurityService.currentUser as TaackUser
                    if (gormEntity instanceof IUserCreated && gormEntity.objectGetUserCreated() == null) {
                        gormEntity.objectSetUserCreated(currentUser)
                    }
                }
            } catch (ignored) {
            }
        })

        TaackSaveService.registerInterfaceCustomSavingClosure(IUserUpdated, { GormEntity gormEntity, Map params ->
            try {
                if (gormEntity.hasChanged()) {
                    TaackUser currentUser = springSecurityService.currentUser as TaackUser
                    if (gormEntity instanceof IUserUpdated) {
                        gormEntity.objectSetUserUpdated(currentUser)
                    }
                }
            } catch (ignored) {
            }
        })

    }


}
