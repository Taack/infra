package taack.ui.base.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.UiTableSpecifier.SelectMode
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.style.EnumStyle

import java.text.NumberFormat

@CompileStatic
class UiTableVisitorImpl implements IUiTableVisitor {
    @Override
    void visitTable(Class aClass, SelectMode selectMode = null) {

    }

    @Override
    void visitTableEnd() {

    }

    @Override
    void visitColumnEnd() {

    }

    @Override
    void visitHeader() {

    }

    @Override
    void visitHeaderEnd() {

    }

    @Override
    void visitRow(Object current, Style style, boolean hasChildren) {

    }

    @Override
    void visitRowEnd() {

    }


    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo fieldInfo, final ColumnHeaderFieldSpec.DefaultSortingDirection defaultDirection) {

    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields, final ColumnHeaderFieldSpec.DefaultSortingDirection defaultDirection) {

    }

    @Override
    void visitFieldHeader(String i18n) {

    }

    @Override
    void visitRowColumnEnd() {

    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan) {

    }

    @Override
    void visitRowField(FieldInfo fieldInfo, String format, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(GetMethodReturn fieldInfo, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(String value, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(Long value, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(BigDecimal value, String format, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(Date value, String format, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitSortableFieldHeader(String i18n, String controller, String action, Map<String, ?> params, Map<String, ?> additionalParams) {

    }

    @Override
    void visitPaginate(Number max, Number offset, Number count) {

    }

    @Override
    void visitRowIndent() {

    }

    @Override
    void visitRowIndentEnd() {

    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo field) {

    }

    @Override
    void visitRowGroupHeader(Object groups, MethodClosure show, Long id) {

    }

    @Override
    void visitRowGroupFooter(String content) {

    }

    @Override
    void visitRowField(Map value, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(EnumStyle value, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style, String controller, String action, Long id) {

    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {

    }

    @Override
    void visitFooterButton(String i18n, String controller, String action, Long id, Map<String, ?> additionalParams) {

    }

    @Override
    void visitRowLink(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitTableWithoutFilter(Class aClass, SelectMode selectMode) {

    }
}
