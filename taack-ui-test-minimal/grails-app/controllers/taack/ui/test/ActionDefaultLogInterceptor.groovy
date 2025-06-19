package taack.ui.test

import grails.artefact.Interceptor
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class ActionDefaultLogInterceptor implements Interceptor {

//    ActionDefaultLogInterceptor() {
//        matchAll().excludes(controller:'taackLogin')
//    }

    boolean before() {
        println "$params"
        true
    }

    boolean after() { true }

    void afterView() {}
}
