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
import taack.ui.dsl.menu.UiMenuVisitor

@CompileStatic
class UiBlockVisitor extends UiMenuVisitor implements IUiBlockVisitor {

    @Override
    void visitBlock() {

    }

    @Override
    void visitBlockEnd() {

    }

    @Override
    void visitBlockHeader() {

    }

    @Override
    void visitBlockHeaderEnd() {

    }

    @Override
    void visitRow() {

    }

    @Override
    void visitRowEnd() {

    }

    @Override
    void visitCol(BlockSpec.Width width) {

    }

    @Override
    void visitInnerColBlockEnd() {

    }

    @Override
    void visitModal() {

    }

    @Override
    void visitModalEnd() {

    }

    @Override
    void visitCloseTitle() {

    }

    @Override
    void visitAjaxBlock(String id) {

    }

    @Override
    void visitAjaxBlockEnd() {

    }

    @Override
    void visitForm(BlockSpec.Width width) {

    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {

    }

    @Override
    void visitShow(BlockSpec.Width width) {

    }

    @Override
    void visitTable(String id, BlockSpec.Width width) {

    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, BlockSpec.Width width) {

    }

    @Override
    void visitTableFilterEnd(UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitChart(BlockSpec.Width width) {

    }

    @Override
    void visitChartEnd(UiChartSpecifier chartSpecifier) {

    }

    @Override
    void visitDiagram(BlockSpec.Width width) {

    }

    @Override
    void visitDiagramFilter(UiFilterSpecifier filterSpecifier, BlockSpec.Width width) {

    }

    @Override
    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier, BlockSpec.Width width) {

    }

    @Override
    void visitCloseModal(String id, String value, FieldInfo[] fields) {

    }

    @Override
    void visitBlockTab(String i18n) {

    }

    @Override
    void visitBlockTabEnd() {

    }

    @Override
    void visitBlockTabs(BlockSpec.Width width) {

    }

    @Override
    void visitBlockTabsEnd() {

    }

    @Override
    void visitCustom(String html, Style style, BlockSpec.Width width) {

    }

    @Override
    void visitShowEnd(UiShowSpecifier uiShowSpecifier) {

    }

    @Override
    void visitCloseModalAndUpdateBlock() {

    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {

    }

    @Override
    void visitHtmlBlock(String html, Style style) {

    }

    @Override
    Map getParameterMap() {
        return null
    }
}
