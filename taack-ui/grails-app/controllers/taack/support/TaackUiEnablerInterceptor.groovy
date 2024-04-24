package taack.support

import grails.artefact.Interceptor
import grails.compiler.GrailsCompileStatic
import org.grails.web.util.WebUtils
import org.springframework.security.core.context.SecurityContextHolder
import taack.render.TaackUiEnablerService

@GrailsCompileStatic
class TaackUiEnablerInterceptor implements Interceptor {

    TaackUiEnablerInterceptor() {
        matchAll()
                .excludes(controller: 'assets')
                .excludes(controller: 'errors')
    }

    int order = HIGHEST_PRECEDENCE

    TaackUiEnablerService taackUiEnablerService

    boolean before() {
        final String c = controllerName
        final String a = actionName
        final request = WebUtils.retrieveGrailsWebRequest().getCurrentRequest()
        if (c && a) {
            try {
                taackUiEnablerService.checkAccess()
            } catch (e) {
                log.error "Access Denied: $c $a ${e.message}, ${SecurityContextHolder.getContext().getAuthentication().name}, $params"
                return false
            }
        } else {
            log.warn "Unknown Controller or Action ${request.remoteHost}|${request.getHeader('user-agent')} $params"
        }
        return true
    }

    boolean after() { true }
}
