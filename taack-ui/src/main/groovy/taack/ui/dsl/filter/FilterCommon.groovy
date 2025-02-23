package taack.ui.dsl.filter


import taack.ui.dsl.filter.expression.FilterExpression

class FilterCommon {
    final IUiFilterVisitor filterVisitor

    void section(final String i18n = null,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SectionSpec) final Closure closure) {
        if (i18n) filterVisitor.visitSection(i18n)
        closure.delegate = new SectionSpec(filterVisitor)
        closure.call()
        if (i18n) filterVisitor.visitSectionEnd()
    }

    FilterCommon(IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
    }

}
