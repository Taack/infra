package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import taack.render.TaackUiService
import taack.ui.dsl.UiMenuSpecifier

@GrailsCompileStatic
@Secured(["permitAll"])
class TaackContextMenuController implements WebAttributes {

    def index() {
        UiMenuSpecifier m = TaackUiService.contextualMenuClosureFromClassName(params.get('className') as String, params.get('fieldName') as String)
        render(contentType: 'text/html', text: m ? TaackUiService.visitContextualMenu(m) : '')
    }
}