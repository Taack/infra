package taack.ui.base

import groovy.transform.CompileStatic
import taack.ui.base.chart.ChartSpec
import taack.ui.base.chart.IUiChartVisitor

@CompileStatic
final class UiChartSpecifier {
    Closure closure

    UiChartSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChartSpec) Closure closure) {
        this.closure = closure
        this
    }

    void visitChart(final IUiChartVisitor chartVisitor) {
        if (chartVisitor && closure) {
            chartVisitor.visitChart()
            closure.delegate = new ChartSpec(chartVisitor)
            closure.call()
            chartVisitor.visitChartEnd()
        }
    }
}