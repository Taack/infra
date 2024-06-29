package taack.ui.test

import grails.compiler.GrailsCompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.domain.TaackSearchService
import taack.ui.dsl.UiBlockSpecifier

@GrailsCompileStatic
class RootSearchService {
    TaackSearchService taackSearchService

    UiBlockSpecifier buildSearchBlock(String q) {
        taackSearchService.search(q, RootController.&search as MethodClosure)
    }
}
