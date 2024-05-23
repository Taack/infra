package taack.ui

import groovy.transform.CompileStatic

@CompileStatic
enum ThemeMode {
    DARK, LIGHT
}

@CompileStatic
enum ThemeSize {
    SM, NONE
}

@CompileStatic
enum ThemeName {
    PURE, BOOTSTRAP
}

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