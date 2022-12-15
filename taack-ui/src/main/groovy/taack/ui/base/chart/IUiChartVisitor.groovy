package taack.ui.base.chart

import groovy.transform.CompileStatic

@CompileStatic
interface IUiChartVisitor {

    void visitChart()

    void visitChartEnd()

    void dataset(String i18nLabel, ChartDatasetSpec.Color chartColor, List<BigDecimal> data)

    void dataset(String i18nLabel, String stackId, ChartDatasetSpec.Color chartColor, List<BigDecimal> data)

    void visitBarChart(List<String> i18nLabels, ChartSpec.Option option)

    void visitLineChart(List<String> i18nLabels, ChartSpec.Option option)

    void visitBarChartEnd()
}