package taack.ui.test

import crew.config.BusinessUnit
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import crew.Role
import crew.User
import grails.compiler.GrailsCompileStatic
import org.springframework.beans.factory.annotation.Value
import crew.UserRole

@GrailsCompileStatic
@Component
class BootStrap {

    @Autowired
    SpringSecurityService springSecurityService

    @Value('${taack.admin.password}')
    String adminPassword

    def init = { servletContext ->
        createDefaultRoleAndUser()
    }
    def destroy = {
    }

    def createDefaultRoleAndUser() {
        log.info "Creating default user and role ..."
        User.withNewTransaction {
            def r = Role.findByAuthority("ROLE_ADMIN")
            if (!r) {
                r = new Role(authority: "ROLE_ADMIN")
                r.save(flush: true)
                if (r.hasErrors()) log.error "${r.errors}"
            }
            def u = User.findByUsername("admin")
            if (!u) {
                u = new User(username: "admin", password: springSecurityService.encodePassword(adminPassword), businessUnit: BusinessUnit.IT)
                u.save(flush: true)
                if (u.hasErrors()) log.error "${u.errors}"
                def ur = UserRole.create(u, r, true)
                if (ur.hasErrors()) log.error "${ur.errors}"
            }
            u
        }
    }
}
