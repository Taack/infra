package taack.ui.base.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.style.EnumStyle

import java.text.NumberFormat

@CompileStatic
class UiTableVisitorImpl implements IUiTableVisitor {

    @Override
    void visitTable() {

    }

    @Override
    void visitTableWithoutFilter() {

    }

    @Override
    void visitTableEnd() {

    }

    @Override
    void visitHeader() {

    }

    @Override
    void visitHeaderEnd() {

    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {

    }

    @Override
    void visitColumnEnd() {

    }

    @Override
    void visitRow(Style style, boolean hasChildren) {

    }

    @Override
    void visitRowEnd() {

    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {

    }

    @Override
    void visitFieldHeader(String i18n) {

    }

    @Override
    void visitFieldHeader(FieldInfo[] fields) {

    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {

    }

    @Override
    void visitRowColumnEnd() {

    }

    @Override
    void visitRowField(FieldInfo fieldInfo, String format, Style style) {

    }

    @Override
    void visitRowField(GetMethodReturn fieldInfo, String format, Style style) {

    }

    @Override
    void visitRowField(String value, Style style) {

    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowIndent() {

    }

    @Override
    void visitRowIndentEnd() {

    }

    @Override
    void visitGroupFieldHeader(FieldInfo[] fields) {

    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {

    }

    @Override
    void visitRowGroupHeader(String label) {

    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {

    }

    @Override
    void visitRowGroupFooter(String content) {

    }

    @Override
    void visitPaginate(Number max, Number count) {

    }
}
