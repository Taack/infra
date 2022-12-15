package taack.ui.base.chart

import groovy.transform.CompileStatic

@CompileStatic
final class ChartDatasetSpec {
    final IUiChartVisitor chartVisitor

    enum Color {
        red('rgb(255, 99, 132)'),
        orange('rgb(255, 159, 64)'),
        yellow('rgb(255, 205, 86)'),
        green('rgb(75, 192, 192)'),
        blue('rgb(54, 162, 235)'),
        purple('rgb(153, 102, 255)'),
        grey('rgb(201, 203, 207)'),
        gr1('#488f31'),
        gr2('#4f9874'),
        gr3('#79a980'),
        gr4('#9eba91'),
        gr5('#bfcba6'),
        gr6('#f7f1dc'),
        gr7('#eed8b6'),
        gr8('#e8be94'),
        gr9('#e4a377'),
        gr10('#e18562'),
        gr11('#de425b'),
        p1('#005b7e'),
        p2('#007995'),
        p3('#00979d'),
        p4('#00b495'),
        p5('#00d07d'),
        p6('#36ea56')

        Color(final String css) {
            this.css = css
        }

        final String css

        static Color colorFrom(int i) {
            values()[i % values().size()]
        }
    }

    ChartDatasetSpec(final IUiChartVisitor chartVisitor) {
        this.chartVisitor = chartVisitor
    }

    void dataset(final String i18nLabel, final Color chartColor, final List<BigDecimal> data) {
        chartVisitor.dataset(i18nLabel, chartColor, data)
    }

    void dataset(final String i18nLabel, final String stackId, final Color chartColor, final List<BigDecimal> data) {
        chartVisitor.dataset(i18nLabel, stackId, chartColor, data)
    }
}
