package taack.ui.base.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.*
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style

@CompileStatic
class UiBlockVisitor implements IUiBlockVisitor {
    @Override
    void visitBlock() {

    }

    @Override
    void visitBlockEnd() {

    }

    @Override
    void visitInnerBlock(String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitInnerBlockEnd() {

    }

    @Override
    void visitActionStart() {

    }

    @Override
    void visitActionEnd() {

    }

    @Override
    void visitAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, boolean isAjaxRendering) {

    }

    @Override
    void visitOutsideAction(String i18n, ActionIcon actionIcon, String baseUrl, Map<String, ?> params) {

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
    void visitForm(String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {

    }

    @Override
    void visitShow(String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitTable(String id, String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitTableFilter(String id, String i18nFilter, UiFilterSpecifier filterSpecifier, String i18nTable, BlockSpec.Width width) {

    }

    @Override
    void visitTableFilterEnd(UiTableSpecifier tableSpecifier = null) {

    }

    @Override
    void visitChart(String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitChartEnd(UiChartSpecifier chartSpecifier = null) {

    }

    @Override
    void visitDiagram(String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitDiagramFilter(String i18nFilter, UiFilterSpecifier filterSpecifier, String i18n, BlockSpec.Width width) {

    }

    @Override
    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier, BlockSpec.Width width) {

    }

    @Override
    void visitCloseModal(String id, String value, FieldInfo[] fields = null) {

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
    void visitCustom(String i18n, String html, Style style, BlockSpec.Width width) {

    }

    @Override
    void anonymousBlock(BlockSpec.Width width) {

    }

    @Override
    void anonymousBlockEnd() {

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

    }
}
