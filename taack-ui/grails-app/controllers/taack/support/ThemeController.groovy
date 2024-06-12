package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackUiService
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dump.html.theme.ThemeSize

// TODO: Develop the UI
@GrailsCompileStatic
@Secured(["permitAll"])
class ThemeController {

    TaackUiService taackUiService

    def index() {
        ThemeSelector themeSelector = ThemeSelector.fromSession(session)
        taackUiService.show(new UiBlockSpecifier().ui({
            modal {
                form new UiFormSpecifier().ui(themeSelector, {
                    section TaackUiService.tr('theme.choice.label'), {
                        field themeSelector.themeMode_
                        field themeSelector.themeSize_
                    }
                    formAction(this.&changeTheme as MC)
                })
            }
        }))
    }

    def changeTheme() {
        def tm = params['themeMode'] as ThemeMode
        def ts = params['themeSize'] as ThemeSize

        session[ThemeSelector.SESSION_THEME_MODE] = tm.toString()
        session[ThemeSelector.SESSION_THEME_SIZE] = ts.toString()

        taackUiService.ajaxReload()
    }
}
