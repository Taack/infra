package taack.ui.dump.html.theme

import grails.validation.Validateable
import groovy.transform.CompileStatic
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ui.dump.html.theme.ThemeSize

import javax.servlet.http.HttpSession

@CompileStatic
final class ThemeSelector implements Validateable {

    final static String SESSION_THEME_MODE = 'themeMode'
    final static String SESSION_THEME_SIZE = 'themeSize'

    ThemeMode themeMode
    ThemeSize themeSize

    private ThemeSelector(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    static ThemeSelector fromSession(HttpSession session) {
        ThemeMode themeMode =( session[SESSION_THEME_MODE] ?: ThemeMode.LIGHT) as ThemeMode
        ThemeSize themeSize = (session[SESSION_THEME_SIZE] ?: ThemeSize.NONE)  as ThemeSize
        new ThemeSelector(themeMode, themeSize)

    }

    FieldInfo<ThemeMode> getThemeMode_() {
        new FieldInfo<ThemeMode>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeMode'), null), 'themeMode', themeMode)
    }

    FieldInfo<ThemeSize> getThemeSize_() {
        new FieldInfo<ThemeSize>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeSize'), null), 'themeSize', themeSize)
    }

}