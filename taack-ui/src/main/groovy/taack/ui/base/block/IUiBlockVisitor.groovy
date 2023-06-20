package taack.ui.base.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.*
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style

@CompileStatic
interface IUiBlockVisitor {

    void visitBlock()

    void visitBlockEnd()

    void visitInnerBlock(String i18n, BlockSpec.Width width)

    void visitInnerBlockEnd()

    void visitActionStart()

    void visitActionEnd()

    void visitAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ? extends Object> params, boolean isAjaxRendering)

    void visitOutsideAction(String i18n, ActionIcon actionIcon, String baseUrl, Map<String, ? extends Object> params)

    void visitModal()

    void visitModalEnd()

    void visitCloseTitle()

    void visitAjaxBlock(String id)

    void visitAjaxBlockEnd()

    void visitForm(String i18n, BlockSpec.Width width)

    void visitFormEnd(UiFormSpecifier formSpecifier)

    void visitShow(String i18n, BlockSpec.Width width)

    void visitTable(String id, String i18n, BlockSpec.Width width)

    void visitTableEnd(UiTableSpecifier tableSpecifier)

    void visitTableFilter(String id, String i18nFilter, UiFilterSpecifier filterSpecifier, String i18nTable, BlockSpec.Width width)

    void visitTableFilterEnd(UiTableSpecifier tableSpecifier)

    void visitChart(String i18n, BlockSpec.Width width)

    void visitChartEnd(UiChartSpecifier chartSpecifier)

    void visitDiagram(String i18n, BlockSpec.Width width)

    void visitDiagramFilter(String i18nFilter, UiFilterSpecifier filterSpecifier, String i18n, BlockSpec.Width width)

    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier, BlockSpec.Width width)

    void visitCloseModal(String id, String value, FieldInfo[] fields)

    void visitBlockTab(String i18n)

    void visitBlockTabEnd()

    void visitBlockTabs(BlockSpec.Width width)

    void visitBlockTabsEnd()

    void visitCustom(String i18n, String html, Style style, BlockSpec.Width width)

    void anonymousBlock(BlockSpec.Width width)

    void anonymousBlockEnd()

    void visitShowEnd(UiShowSpecifier uiShowSpecifier)

    void visitCloseModalAndUpdateBlock()

    void visitCloseModalAndUpdateBlockEnd()

    void visitHtmlBlock(String html, Style style)

    Map getParameterMap()
}