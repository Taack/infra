package taack.ui.dump.html.theme

import grails.validation.Validateable
import groovy.transform.CompileStatic
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo

import javax.servlet.http.HttpServletRequest

@CompileStatic
final class ThemeSelector implements Validateable {

    final static String SESSION_THEME_MODE = 'themeMode'
    final static String SESSION_THEME_AUTO = 'themeAuto'
    final static String SESSION_THEME_SIZE = 'themeSize'

    ThemeMode themeMode
    ThemeMode themeAuto
    ThemeSize themeSize

    ThemeSelector(ThemeMode themeMode, ThemeMode themeAuto, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeAuto = themeAuto
        this.themeSize = themeSize
    }

    static ThemeSelector fromCookie(HttpServletRequest request) {
        ThemeMode themeMode = (request.cookies?.find { it.name == SESSION_THEME_MODE }?.value as ThemeMode) ?: ThemeMode.NORMAL
        ThemeMode themeAuto = ThemeMode.fromName(request.cookies?.find { it.name == SESSION_THEME_AUTO }?.value)
        ThemeSize themeSize = request.cookies?.find { it.name == SESSION_THEME_SIZE }?.value as ThemeSize ?: ThemeSize.SM

        new ThemeSelector(themeMode, themeAuto, themeSize)
    }

        FieldInfo<ThemeMode> getThemeMode_() {
        new FieldInfo<ThemeMode>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeMode'), null), 'themeMode', themeMode)
    }

    FieldInfo<ThemeSize> getThemeSize_() {
        new FieldInfo<ThemeSize>(new FieldConstraint(null, ThemeSelector.getDeclaredField('themeSize'), null), 'themeSize', themeSize)
    }

}
