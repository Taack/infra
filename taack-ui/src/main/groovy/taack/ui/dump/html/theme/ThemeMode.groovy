package taack.ui.dump.html.theme

import groovy.transform.CompileStatic


@CompileStatic
enum ThemeMode {
    NORMAL('auto'), DARK('dark'), LIGHT('light')

    ThemeMode(String name) {
        this.name = name
    }

    static ThemeMode fromName(String name) {
        if (name == 'dark') DARK
        else LIGHT
    }
    final String name
}