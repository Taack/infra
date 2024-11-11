package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.render.TaackEditorService

@Secured("isAuthenticated()")
@GrailsCompileStatic
class TaackEditorController {

    TaackEditorService taackEditorService

    def tqlDiagram(String script) {
        taackEditorService.asciidocRenderScript(script)
        render "OK"
    }

    def tqlTable(String script) {
        taackEditorService.asciidocRenderScript(script)
        render "OK"
    }

}
