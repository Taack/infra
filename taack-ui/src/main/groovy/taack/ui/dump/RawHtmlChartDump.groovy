package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.chart.ChartDatasetSpec
import taack.ui.dsl.chart.ChartSpec
import taack.ui.dsl.chart.IUiChartVisitor

@CompileStatic
class RawHtmlChartDump implements IUiChartVisitor {
    final private ByteArrayOutputStream out
    final private String chartId
    enum ChartType {
        BAR("bar", true),
        LINE("line", false)

        ChartType(final String typeOfChart, final boolean stacked) {
            this.typeOfChart = typeOfChart
            this.stacked = stacked
        }

        final String typeOfChart
        final boolean stacked
    }

    ChartType chartType = ChartType.BAR

    RawHtmlChartDump(final ByteArrayOutputStream out, final String chartId) {
        this.out = out
        this.chartId = chartId
    }

    final class Dataset {
        final String label
        final String stack
        final ChartDatasetSpec.Color chartColor
        final List<BigDecimal> data

        Dataset(final String label, final ChartDatasetSpec.Color chartColor, final List<BigDecimal> data) {
            this.label = label
            this.chartColor = chartColor
            this.data = data
            this.stack = null
        }

        Dataset(final String label, final String stack, final ChartDatasetSpec.Color chartColor, final List<BigDecimal> data) {
            this.label = label
            this.chartColor = chartColor
            this.data = data
            this.stack = stack
        }
    }

    private List<String> labels
    private ChartSpec.Option option
    final private Stack<Dataset> datasetStack = new Stack<>()
    private boolean isStacked = true

    @Override
    void visitChart() {
        out << """
        <div class="taackChartContainer">
            <canvas id="chart_${chartId}" class="taackChartCanvas"></canvas>
        </div>
        """
    }

    @Override
    void visitBarChart(final List<String> i18nLabels, final ChartSpec.Option option) {
        this.labels = i18nLabels
        this.option = option
    }

    @Override
    void visitLineChart(final List<String> i18nLabels, final ChartSpec.Option option) {
        this.chartType = ChartType.LINE
        this.labels = i18nLabels
        this.option = option
    }

    @Override
    void visitChartEnd() {
        out << """
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <script>
            (function () {
                const ctx = document.querySelector("canvas#chart_${chartId}").getContext('2d');
                const color = Chart.helpers.color;
                new Chart(ctx, {
                    type: '${chartType.typeOfChart}',
                    data: {
                        labels: ['${labels.join("','")}'],
                        datasets: [
        """
        datasetStack.reverse().each {
            out << """
                        {
                            label: '${it.label}',
                            backgroundColor: color('${it.chartColor.css}').alpha(${chartType.stacked?0.5:0.2}).rgbString(),
                            borderColor: '${it.chartColor.css}',
                            borderWidth: 1,
                            data: [${it.data.join(',')}]${it.stack ?',':''}
                            ${it.stack ?"stack: '${it.stack}'":""}
                        },
            """
        }
        out << """
                       ]
                    },
                    options: {
                        responsive: true,
                        legend: {
                            position: 'top',
                        },
                        //title: {
                        //    display: true,
                        //    text: '${}'
                        //},
            """
        if (isStacked)
            out << """
                            scales: {
                                x: {
                                    stacked: ${this.chartType.stacked},
                                },
                                y: {
                                    stacked: ${this.chartType.stacked}
                                }
                            }
                        }
                    });
                })()
            </script>
            """
    }

    @Override
    void dataset(final String i18nLabel, final ChartDatasetSpec.Color chartColor, final List<BigDecimal> data) {
        datasetStack.add(new Dataset(i18nLabel, chartColor, data))
    }

    @Override
    void dataset(final String i18nLabel, final String stackId, final ChartDatasetSpec.Color chartColor, final List<BigDecimal> data) {
        datasetStack.add(new Dataset(i18nLabel, stackId, chartColor, data))
    }

    @Override
    void visitBarChartEnd() {
        // pass through
    }
}
