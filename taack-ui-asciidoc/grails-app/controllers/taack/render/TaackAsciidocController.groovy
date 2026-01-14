package taack.render

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.wysiwyg.Asciidoc

@GrailsCompileStatic
@Secured('isAuthenticated()')
class TaackAsciidocController {

    def renderDiagram(String path) {
        File f = new File(Asciidoc.pathAsciidocGenerated + '/' + path)
        if (f.exists()) {
            String contentType = path.endsWith('png') ? 'image/png':'image/svg+xml'
            response.setContentType(contentType)
            response.setHeader('Content-disposition', "attachment;filename=${URLEncoder.encode(path, 'UTF-8')}")
            response.outputStream << f.bytes
        }
        response.outputStream.flush()
        response.outputStream.close()
    }
}