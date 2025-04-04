package taack.render

import grails.compiler.GrailsCompileStatic
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import taack.ui.dump.html.theme.ThemeSelector

@GrailsCompileStatic
class ThemeService {
    ThemeSelector getThemeSelector() {
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        ThemeSelector.fromCookie(webUtils.request)
    }
}
