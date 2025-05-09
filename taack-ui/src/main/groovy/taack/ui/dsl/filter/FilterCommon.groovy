package taack.ui.dsl.filter

import taack.ast.type.FieldInfo
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.filter.expression.FilterExpression

class FilterCommon {
    final IUiFilterVisitor filterVisitor

    void section(boolean collapse,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SectionSpec) final Closure closure) {
        section(null, collapse, closure)
    }

    void section(final String i18n = null, boolean collapse = false,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SectionSpec) final Closure closure) {
        filterVisitor.visitSection(i18n, collapse)
        closure.delegate = new SectionSpec(filterVisitor)
        closure.call()
        filterVisitor.visitSectionEnd()
    }

    FilterCommon(IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
    }

    void filterFieldExpressionBool(final FilterExpression... filterExpressions) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpressions)
    }

    void orOp(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FilterCommon) final Closure closure) {
        filterVisitor.visitOrOp()
        closure.delegate = new SectionSpec(filterVisitor)
        closure.call()
        filterVisitor.visitOrOpEnd()

    }

    void andOp(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FilterCommon) final Closure closure) {
        filterVisitor.visitAndOp()
        closure.delegate = new SectionSpec(filterVisitor)
        closure.call()
        filterVisitor.visitAndOpEnd()

    }

    void innerFilter(UiFilterSpecifier filterSpecifier, final FieldInfo... fields = null) {
        filterVisitor.visitInnerFilter(filterSpecifier, fields)
    }

}
