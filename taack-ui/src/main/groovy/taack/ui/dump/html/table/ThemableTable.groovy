package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dsl.table.TableOption
import taack.ui.dump.Parameter
import taack.ui.dump.html.element.HTMLDiv
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

    String getTableSized() {
        switch (themeSize) {
            case ThemeSize.NORMAL:
                'table'
                break
            case ThemeSize.LG:
                'table table-xl'
                break
            case ThemeSize.SM:
                'table table-sm'
                break
        }
    }

    IHTMLElement table(IHTMLElement topElement, String blockId, TableOption tableOption) {
        IHTMLElement.HTMLElementBuilder htmlTableBuilder = new HTMLTable().builder.addClasses( "$tableSized table-striped table-hover table-bordered pure-table pure-table-bordered")
        if (tableOption) {
            if (tableOption.headerThemeColor) {
                htmlTableBuilder.addClasses(tableOption.headerThemeColor.cssClassesString)
            }
            if (tableOption.uploadFileAction) {
                htmlTableBuilder.putAttribute('taackDropAction', new Parameter().urlMapped(tableOption.uploadFileAction, tableOption.uploadFileActionParams))
            }
            if (tableOption.stickyColumns > 0) {
                htmlTableBuilder.addClasses('taack-sticky-table')
                htmlTableBuilder.putAttribute('taackStickyColumns', tableOption.stickyColumns.toString())
            }
        }
        htmlTableBuilder.putAttribute('taackTableId', blockId)
        HTMLTable htmlTable = htmlTableBuilder.build() as HTMLTable
        // Sticky columns need a dedicated horizontal scroll container since the block itself only scrolls vertically
        if (tableOption != null && tableOption.stickyColumns > 0) {
            IHTMLElement scroller = new HTMLDiv().builder.addClasses('taack-sticky-scroll').build()
            topElement.addChildren(scroller)
            scroller.addChildren(htmlTable)
        } else {
            topElement.addChildren(htmlTable)
        }
        htmlTable
    }
}
