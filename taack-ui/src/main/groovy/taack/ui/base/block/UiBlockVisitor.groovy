package taack.ui.base.block

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ui.IEnumOptions
import taack.ui.base.*
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.menu.MenuSpec
import taack.ui.base.menu.UiMenuVisitor

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
    void visitInnerRowBlock() {

    }

    @Override
    void visitInnerRowBlockEnd() {

    }

    @Override
    void visitInnerColBlock(BlockSpec.Width width) {

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
