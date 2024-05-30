package taack.ui.dump.html.table

import groovy.transform.CompileStatic

@CompileStatic
interface ITableTheme {
    String getTableClasses()
    String getTableHeadClasses()
    String getTableRowClasses(boolean isOod)
    String getTablePrimary()
    String getTableActive()
    String getTableSuccess()
    String getTableDanger()
    String getTableWarning()
    String getTableInfo()
    String getTableLight()
    String getTableDark()
}


