package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import taack.render.TaackUiService
import taack.ui.dsl.UiMenuSpecifier

@GrailsCompileStatic
@Secured(["permitAll"])
class TaackContextMenuController implements WebAttributes {

    TaackUiService taackUiService

    def index() {
        UiMenuSpecifier m = taackUiService.contextualMenuClosureFromClassName(params.get('className') as String)
        render(contentType: 'text/html', text: m ? TaackUiService.visitContextualMenu(m) : '')
    }
}