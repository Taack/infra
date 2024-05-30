package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

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
