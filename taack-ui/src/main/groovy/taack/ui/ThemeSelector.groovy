package taack.ui

import grails.validation.Validateable
import groovy.transform.CompileStatic
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ui.dump.theme.ThemeMode
import taack.ui.dump.theme.ThemeName
import taack.ui.dump.theme.ThemeSize

import javax.servlet.http.HttpSession

@CompileStatic
final class ThemeSelector implements Validateable {

    final static String SESSION_THEME_MODE = 'themeMode'
    final static String SESSION_THEME_SIZE = 'themeSize'
    final static String SESSION_THEME_NAME = 'themeName'

    ThemeMode themeMode
    ThemeSize themeSize
    ThemeName themeName

    private ThemeSelector(ThemeMode themeMode, ThemeSize themeSize, ThemeName themeName) {
        this.themeMode = themeMode
        this.themeSize = themeSize
        this.themeName = themeName
    }

    static ThemeSelector fromSession(HttpSession session) {
        ThemeMode themeMode =( session[SESSION_THEME_MODE] ?: ThemeMode.LIGHT) as ThemeMode
        ThemeSize themeSize = (session[SESSION_THEME_SIZE] ?: ThemeSize.NONE)  as ThemeSize
        ThemeName themeName = (session[SESSION_THEME_NAME] ?: ThemeName.PURE) as ThemeName
        new ThemeSelector(themeMode, themeSize, themeName)

    }

    FieldInfo<ThemeMode> getThemeMode_() {
        new FieldInfo<ThemeMode>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeMode'), null), 'themeMode', themeMode)
    }

    FieldInfo<ThemeSize> getThemeSize_() {
        new FieldInfo<ThemeSize>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeSize'), null), 'themeSize', themeSize)
    }

    FieldInfo<ThemeName> getThemeName_() {
        new FieldInfo<ThemeName>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeName'), null), 'themeName', themeName)
    }
}
