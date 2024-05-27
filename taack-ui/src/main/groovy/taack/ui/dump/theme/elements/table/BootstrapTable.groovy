package taack.ui.dump.theme.elements.table

import groovy.transform.CompileStatic

@CompileStatic
final class BootstrapTable extends TableThemeImpl {

    final ThemeMode themeMode
    final ThemeSize themeSize

    BootstrapTable(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    @Override
    String getTableClasses() {
        return " table " + (themeMode == ThemeMode.DARK ? " table-dark":"") + (themeSize == ThemeSize.SM ? " table-sm":"") + " "
    }
}
