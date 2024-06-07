package taack.ui.test

import grails.artefact.Interceptor
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import org.grails.web.util.WebUtils

@GrailsCompileStatic
class ActionDefaultLogInterceptor implements Interceptor {

    ActionDefaultLogInterceptor() {
        matchAll().excludes(controller:"login")
    }

    SpringSecurityService springSecurityService

    boolean before() {
        println "$params"
        true
    }

    boolean after() { true }

    void afterView() {}
}
