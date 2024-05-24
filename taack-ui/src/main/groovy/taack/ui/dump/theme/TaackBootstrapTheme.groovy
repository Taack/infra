package taack.ui.dump.theme

import groovy.transform.CompileStatic

@CompileStatic
final class TaackBootstrapTheme extends TableThemeImpl {

    final ThemeMode themeMode
    final ThemeSize themeSize

    TaackBootstrapTheme(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    @Override
    String getTableClasses() {
        return " table " + (themeMode == ThemeMode.DARK ? " table-dark":"") + (themeSize == ThemeSize.SM ? " table-sm":"") + " "
    }
}
