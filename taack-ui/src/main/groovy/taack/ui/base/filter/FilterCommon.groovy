package taack.ui.base.filter

import taack.ui.base.filter.expression.FilterExpression

class FilterCommon {
    final IUiFilterVisitor filterVisitor

    FilterCommon(IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
    }

}
