package taack.ui.dump.theme

import groovy.transform.CompileStatic

@CompileStatic
class TaackPureCSSTheme extends TableThemeImpl {
    @Override
    String getTableClasses() {
        return " pure-table pure-table-bordered "
    }

    @Override
    String getTableRowClasses(boolean isOdd) {
        return isOdd ? " pure-table-odd " : ""
    }
}
