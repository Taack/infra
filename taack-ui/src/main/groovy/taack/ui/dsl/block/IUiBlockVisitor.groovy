package taack.ui.dsl.block

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiKanbanSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style
import taack.ui.dsl.menu.IUiMenuVisitor

@CompileStatic
interface IUiBlockVisitor extends IUiMenuVisitor {

    boolean doRenderElement()

    boolean doRenderLayoutElement()

    boolean doRenderElement(String id)

    void visitBlock()

    void visitBlockEnd()

    void visitBlockHeader()

    void visitBlockHeaderEnd()

    void visitRow()

    void visitRowEnd()

    void visitCol(BlockSpec.Width width)

    void visitColEnd()

    void visitModal(boolean reloadWhenClose)

    void visitModalEnd()

    String getExplicitAjaxBlockId()

    void setExplicitAjaxBlockId(String id)

    void visitAjaxBlock(String id)

    void visitAjaxBlockEnd()

    void visitForm(UiFormSpecifier formSpecifier)

    void visitShow(UiShowSpecifier uiShowSpecifier)

    void visitTable(String id, UiTableSpecifier tableSpecifier)

    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, UiTableSpecifier tableSpecifier)

    void visitDiagram(UiDiagramSpecifier diagramSpecifier)

    void visitDiagramFilter(UiDiagramSpecifier diagramSpecifier, UiFilterSpecifier filterSpecifier)

    void visitCloseModal(Map<String, String> idValueMap, FieldInfo[] fields)

    void visitBlockTab(String i18n)

    void visitBlockTabEnd()

    void visitBlockTabs()

    void visitBlockTabsEnd()

    void visitBlockPoke(boolean update)

    void visitBlockPokeEnd()

    void visitCustom(String html, Style style)

    void visitCloseModalAndUpdateBlock()

    void visitCloseModalAndUpdateBlockEnd()

    void visitHtmlBlock(String html, Style style)

    void visitIframe(String url, String cssHeight)

    void visitPoll(int millis, MethodClosure polledMethod)

    void visitPollEnd()

    Map getParameterMap()

    void setRenderTab(boolean isRender)

    void visitKanban(String id, UiKanbanSpecifier kanbanSpecifier)

    void visitKanbanFilter(String aId, UiFilterSpecifier filterSpecifier, UiKanbanSpecifier kanbanSpecifier)
}