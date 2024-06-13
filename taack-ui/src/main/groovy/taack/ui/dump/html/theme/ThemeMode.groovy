package taack.ui.dump.html.theme

import groovy.transform.CompileStatic


@CompileStatic
enum ThemeMode {
    NORMAL('auto'), DARK('dark'), LIGHT('light')

    ThemeMode(String name) {
        this.name = name
    }

    final String name
}