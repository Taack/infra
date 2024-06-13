package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class ThemableTable {

    final ThemeMode themeMode
    final ThemeSize themeSize

    ThemableTable(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    static IHTMLElement table(IHTMLElement topElement, String blockId) {
        HTMLTable htmlTable = new HTMLTable()
        htmlTable.setTaackTag(TaackTag.TABLE)
        htmlTable.classes = ['table', 'table-striped', 'table-hover', 'table-bordered']
        htmlTable.attributes.put('taackTableId', blockId)
        topElement.addChildren(
                htmlTable
        )
        htmlTable
    }
}
