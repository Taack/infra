package cms

import cms.dsl.parser.Parser
import cms.dsl.parser.exception.ParserException
import cms.dsl.parser.exception.TokenizerException
import cms.dsl.parser.exception.WrongDataException
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import org.asciidoctor.SafeMode
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dump.Parameter
import taack.wysiwyg.Asciidoc
import taack.wysiwyg.Markdown

@Transactional
@GrailsCompileStatic
class CmsHtmlGeneratorService {

    String translate(final String body, final String lang, boolean asciidoc = false, MethodClosure fileRoot = null, Long id = null) {
//        translateNoMatcher(translateExpression(body, lang))
        translateExpression(translateNoMatcher(body, asciidoc, fileRoot, id), lang)
    }

    static String translateExpression(String body, String lang) {
        int index = body.indexOf('${')
        if (index == -1) return body
        int previousIndex = 0
        StringBuffer res = new StringBuffer()
        Parser p = new Parser(lang)
        while (index != -1) {
            res.append(body.substring(previousIndex, index))
            int indexTo = body.indexOf('}', index)
            if (indexTo == -1) {
                res.append("<span class='error'>\${!!! No Closing brace !!!</span>")
            } else {
                String expression = body.substring(index + 2, indexTo)
                try {
                    String result = p.parse(expression)
                    res.append(result)
                } catch (TokenizerException te) {
                    res.append("<span style='color: red'>\${$expression}<-${te.message}, ${te.problematicString}</span>")
                } catch (ParserException pe) {
                    res.append("<span style='color: red'>\${$expression}<-${pe.message}, ${pe.lookahead}</span>")
                } catch (WrongDataException wde) {
                    res.append("<span style='color: red'>\${$expression}<-${wde.message}, ${wde.id}</span>")
                }
            }
            previousIndex = indexTo == -1 ? index + 2:indexTo + 1
            index = body.indexOf('${', previousIndex)
        }
        if (previousIndex > 0) res.append(body.substring(previousIndex))
        res.toString()
    }

    static String translateNoMatcher(String body, boolean asciidoc = false, MethodClosure fileRoot = null, Long id = null) {
        try {
            String urlFileRoot = fileRoot && id ? new Parameter().urlMapped(fileRoot, [id: id]) : null
            return asciidoc ? Asciidoc.getContentHtml(body, urlFileRoot, SafeMode.SERVER): Markdown.getContentHtml(body)
        } catch (ignored) {
            return "Grr ${ignored}"
        }
    }
}
