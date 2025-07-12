package taack.ui.test


import grails.artefact.Interceptor
import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import org.grails.web.util.WebUtils
import org.springframework.boot.jdbc.DataSourceUnwrapper

import javax.sql.DataSource

@GrailsCompileStatic
class ActionDefaultLogInterceptor implements Interceptor {

    ActionDefaultLogInterceptor() {
        matchAll()
                .excludes(action: 'getPluginLogo')
                .excludes(action: 'preview')
                .excludes(action: 'mediaPreview')
                .excludes(action: 'doc')
    }

    SpringSecurityService springSecurityService

    boolean before() {
        final String c = params.get('controller')
        final String a = params.get('action')
        def request = WebUtils.retrieveGrailsWebRequest().getCurrentRequest()
        if (c && a) {
            try {
                Long userId = springSecurityService.currentUserId as Long

                log.info "AUOINT ${c} ${a} ${userId} ${request.post ? 'post' : request.get ? 'get' : 'unknown'} ${request.remoteHost}|${request.getHeader('user-agent')} $params ${request.forwardURI}"
            } catch (ignored) {
                log.error "AUOEXP ${params.get('controller')} ${params.get('action')} ${ignored.message}"
            }
        } else {
            log.info "AUOOTR ${request.remoteHost}|${request.getHeader('user-agent')} $params"
        }
        true
    }

    boolean after() { true }

}
