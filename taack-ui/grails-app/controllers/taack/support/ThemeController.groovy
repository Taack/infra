package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackUiService
import taack.ui.ThemeMode
import taack.ui.ThemeName
import taack.ui.ThemeSize
import taack.ui.ThemeSelector
import taack.ui.base.UiBlockSpecifier
import taack.ui.base.UiFormSpecifier
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
                        field themeSelector.themeName_
                    }
                    formAction(this.&changeTheme as MC)
                })
            }
        }))
    }

    def changeTheme() {
        def tm = params['themeMode'] as ThemeMode
        def ts = params['themeSize'] as ThemeSize
        def bs = params['themeName'] as ThemeName

        session[ThemeSelector.SESSION_THEME_MODE] = tm.toString()
        session[ThemeSelector.SESSION_THEME_SIZE] = ts.toString()
        session[ThemeSelector.SESSION_THEME_NAME] = bs.toString()

        taackUiService.ajaxReload()
    }
}
