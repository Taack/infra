package taack.ui.dump.html.theme

import groovy.transform.CompileStatic

@CompileStatic
enum ThemeLook {
    TAACK(null), WORKBENCH('workbench'), LEDGER('ledger')

    // Not called name: EnumOptions would label the option with it instead of enum.value.<constant>.
    ThemeLook(String themeAttribute) {
        this.themeAttribute = themeAttribute
    }

    /** The value of the page's {@code data-taack-theme} attribute, or null when the look needs none. */
    final String themeAttribute

    /** The look a cookie names, or TAACK when it names none or a look that was removed. */
    static ThemeLook fromName(String name) {
        values().find { it.name() == name } ?: TAACK
    }
}
