package taack.ui.dsl.chart

import groovy.transform.CompileStatic

@CompileStatic
final class ChartSpec {
    final IUiChartVisitor chartVisitor
    final ChartDatasetSpec datasetSpec
    static class Option {
        final String title
        final boolean stacked

        Option(final String title = null, final boolean stacked = true) {
            this.title = title
            this.stacked = stacked
        }
    }

    ChartSpec(final IUiChartVisitor chartVisitor) {
        this.chartVisitor = chartVisitor
        this.datasetSpec = new ChartDatasetSpec(chartVisitor)
    }

    void barChart(List<String> i18nLabels, Option option = new Option(),
                  @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChartDatasetSpec) Closure closure) {
        chartVisitor.visitBarChart(i18nLabels, option)
        closure.delegate = datasetSpec
        closure.call()
        chartVisitor.visitBarChartEnd()
    }

    void lineChart(List<String> i18nLabels, Option option = new Option(),
                   @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChartDatasetSpec) Closure closure) {
        chartVisitor.visitLineChart(i18nLabels, option)
        closure.delegate = datasetSpec
        closure.call()
        chartVisitor.visitBarChartEnd()
    }
}
