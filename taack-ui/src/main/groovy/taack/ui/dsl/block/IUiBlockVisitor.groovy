package taack.ui.dsl.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.UiChartSpecifier
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style
import taack.ui.dsl.menu.IUiMenuVisitor

@CompileStatic
interface IUiBlockVisitor extends IUiMenuVisitor {

    boolean doRenderElement(String id)

    void visitBlock()

    void visitBlockEnd()

    void visitBlockHeader()

    void visitBlockHeaderEnd()

    void visitRow()

    void visitRowEnd()

    void visitCol(BlockSpec.Width width)

    void visitColEnd()

    void visitModal()

    void visitModalEnd()

    void visitAjaxBlock(String id)

    void visitAjaxBlockEnd()

    void visitForm(UiFormSpecifier formSpecifier)

    void visitShow(UiShowSpecifier uiShowSpecifier)

    void visitTable(String id, UiTableSpecifier tableSpecifier)

    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, UiTableSpecifier tableSpecifier)

    void visitChart(UiChartSpecifier chartSpecifier)

    void visitDiagram(UiDiagramSpecifier diagramSpecifier)

    void visitDiagramFilter(UiDiagramSpecifier diagramSpecifier, UiFilterSpecifier filterSpecifier)

    void visitCloseModal(String id, String value, FieldInfo[] fields)

    void visitBlockTab(String i18n)

    void visitBlockTabEnd()

    void visitBlockTabs()

    void visitBlockTabsEnd()

    void visitCustom(String html, Style style)

    void visitCloseModalAndUpdateBlock()

    void visitCloseModalAndUpdateBlockEnd()

    void visitHtmlBlock(String html, Style style)

    Map getParameterMap()
}