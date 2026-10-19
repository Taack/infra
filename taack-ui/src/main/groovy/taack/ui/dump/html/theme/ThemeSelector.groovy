package taack.ui.dump.html.theme

import grails.validation.Validateable
import groovy.transform.CompileStatic
import jakarta.servlet.http.HttpServletRequest
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo


@CompileStatic
final class ThemeSelector implements Validateable {

    final static String SESSION_THEME_MODE = 'themeMode'
    final static String SESSION_THEME_AUTO = 'themeAuto'
    final static String SESSION_THEME_SIZE = 'themeSize'
    final static String SESSION_THEME_LOOK = 'themeLook'

    ThemeMode themeMode
    ThemeMode themeAuto
    ThemeSize themeSize
    ThemeLook themeLook

    ThemeSelector(ThemeMode themeMode, ThemeMode themeAuto, ThemeSize themeSize, ThemeLook themeLook = ThemeLook.TAACK) {
        this.themeMode = themeMode
        this.themeAuto = themeAuto
        this.themeSize = themeSize
        this.themeLook = themeLook
    }

    static ThemeSelector fromCookie(HttpServletRequest request) {
        ThemeMode themeMode = (request.cookies?.find { it.name == SESSION_THEME_MODE }?.value as ThemeMode) ?: ThemeMode.NORMAL
        ThemeMode themeAuto = ThemeMode.fromName(request.cookies?.find { it.name == SESSION_THEME_AUTO }?.value)
        ThemeSize themeSize = request.cookies?.find { it.name == SESSION_THEME_SIZE }?.value as ThemeSize ?: ThemeSize.SM
        ThemeLook themeLook = ThemeLook.fromName(request.cookies?.find { it.name == SESSION_THEME_LOOK }?.value)

        new ThemeSelector(themeMode, themeAuto, themeSize, themeLook)
    }

        FieldInfo<ThemeMode> getThemeMode_() {
        new FieldInfo<ThemeMode>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeMode'), null), 'themeMode', themeMode)
    }

    FieldInfo<ThemeSize> getThemeSize_() {
        new FieldInfo<ThemeSize>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeSize'), null), 'themeSize', themeSize)
    }

    FieldInfo<ThemeLook> getThemeLook_() {
        new FieldInfo<ThemeLook>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeLook'), null), 'themeLook', themeLook)
    }

}
