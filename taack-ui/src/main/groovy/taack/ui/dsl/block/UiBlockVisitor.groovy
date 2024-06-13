package taack.ui.dsl.block

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ui.IEnumOptions
import taack.ui.dsl.*
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.menu.MenuSpec

@CompileStatic
class UiBlockVisitor implements IUiBlockVisitor {

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
    void visitColEnd() {

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
    void visitForm(UiFormSpecifier formSpecifier) {

    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {

    }

    @Override
    void visitShow() {

    }

    @Override
    void visitTable(String id, UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitChart() {

    }

    @Override
    void visitChartEnd(UiChartSpecifier chartSpecifier) {

    }

    @Override
    void visitDiagram() {

    }

    @Override
    void visitDiagramFilter(UiFilterSpecifier filterSpecifier) {

    }

    @Override
    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier) {

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
    void visitBlockTabs() {

    }

    @Override
    void visitBlockTabsEnd() {

    }

    @Override
    void visitCustom(String html, Style style) {

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

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {

    }

    @Override
    void visitMenuLabelEnd() {

    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {

    }

    @Override
    void visitMenuStartEnd() {

    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {

    }

    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {

    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {

    }

    @Override
    void visitMenuSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal) {

    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {

    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {

    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {

    }
}