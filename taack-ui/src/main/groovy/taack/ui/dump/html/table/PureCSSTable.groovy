package taack.ui.dump.html.table

import groovy.transform.CompileStatic

@CompileStatic
class PureCSSTable extends TableThemeImpl {
    @Override
    String getTableClasses() {
        return " pure-table pure-table-bordered "
    }

    @Override
    String getTableRowClasses(boolean isOdd) {
        return isOdd ? " pure-table-odd " : ""
    }
}