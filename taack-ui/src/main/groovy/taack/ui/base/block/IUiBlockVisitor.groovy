package taack.ui.base.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.*
import taack.ui.base.common.Style
import taack.ui.base.menu.IUiMenuVisitor

@CompileStatic
interface IUiBlockVisitor extends IUiMenuVisitor {

    void visitBlock()

    void visitBlockEnd()

    void visitBlockHeader()

    void visitBlockHeaderEnd()

    void visitInnerBlock(BlockSpec.Width width)

    void visitInnerBlockEnd()

    void visitModal()

    void visitModalEnd()

    void visitCloseTitle()

    void visitAjaxBlock(String id)

    void visitAjaxBlockEnd()

    void visitForm(BlockSpec.Width width)

    void visitFormEnd(UiFormSpecifier formSpecifier)

    void visitShow(BlockSpec.Width width)

    void visitTable(String id, BlockSpec.Width width)

    void visitTableEnd(UiTableSpecifier tableSpecifier)

    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, BlockSpec.Width width)

    void visitTableFilterEnd(UiTableSpecifier tableSpecifier)

    void visitChart(BlockSpec.Width width)

    void visitChartEnd(UiChartSpecifier chartSpecifier)

    void visitDiagram(BlockSpec.Width width)

    void visitDiagramFilter(UiFilterSpecifier filterSpecifier, BlockSpec.Width width)

    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier, BlockSpec.Width width)

    void visitCloseModal(String id, String value, FieldInfo[] fields)

    void visitBlockTab(String i18n)

    void visitBlockTabEnd()

    void visitBlockTabs(BlockSpec.Width width)

    void visitBlockTabsEnd()

    void visitCustom(String html, Style style, BlockSpec.Width width)

    void anonymousBlock(BlockSpec.Width width)

    void anonymousBlockEnd()

    void visitShowEnd(UiShowSpecifier uiShowSpecifier)

    void visitCloseModalAndUpdateBlock()

    void visitCloseModalAndUpdateBlockEnd()

    void visitHtmlBlock(String html, Style style)

    Map getParameterMap()
}