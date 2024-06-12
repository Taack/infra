package taack.ui.dsl.filter


import taack.ui.dsl.filter.expression.FilterExpression

class FilterCommon {
    final IUiFilterVisitor filterVisitor

    FilterCommon(IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
    }

}
