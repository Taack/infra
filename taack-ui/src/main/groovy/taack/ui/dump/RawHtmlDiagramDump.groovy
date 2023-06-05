package taack.ui.dump

import groovy.transform.CompileStatic
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.labels.ItemLabelAnchor
import org.jfree.chart.labels.ItemLabelPosition
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PiePlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.CategoryItemRenderer
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.jfree.chart.renderer.category.LineAndShapeRenderer
import org.jfree.chart.ui.TextAnchor
import org.jfree.chart.util.SortOrder
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.DefaultPieDataset
import org.jfree.svg.SVGGraphics2D
import taack.ui.base.diagram.DiagramBaseSpec
import taack.ui.base.diagram.IUiDiagramVisitor

import java.awt.Font
import java.awt.geom.Rectangle2D

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    final private String diagramId

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final String diagramId) {
        this.out = out
        this.diagramId = diagramId
    }

    enum DiagramBase {
        SVG,
        PNG
    }
    DiagramBase diagramBase

    private BigDecimal diagramWidth
    private BigDecimal diagramHeight
    private List<String> xLabels
    private String diagramTitle
    private Map<String, List<BigDecimal>> yDataPerKey
    private JFreeChart chart

    @Override
    void visitDiagram() {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitSvgDiagram(DiagramBaseSpec.HeightWidthRadio radio) {
        this.diagramBase = DiagramBase.SVG
        this.diagramWidth = 480.0 / radio.radio
        this.diagramHeight = 480.0
    }

    @Override
    void visitSvgDiagramEnd() {
        if (diagramBase == DiagramBase.SVG) {
            out << "<svg viewBox='0 0 ${diagramWidth} ${diagramHeight}'>"

            SVGGraphics2D graphics2D = new SVGGraphics2D(diagramWidth.toDouble(), diagramHeight.toDouble())
            chart.draw(graphics2D, new Rectangle2D.Double(0, 0, diagramWidth.toDouble(), diagramHeight.toDouble()), null)
            out << graphics2D.SVGDocument

            out << "</svg>"
        }
    }

    @Override
    void visitPngDiagram() {
        this.diagramBase = DiagramBase.PNG
    }

    @Override
    void visitBarDiagram(List<String> xLabels, String title) {
        this.xLabels = xLabels
        this.diagramTitle = title
        this.yDataPerKey = [:]
    }

    @Override
    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset()
        yDataPerKey.each { Map.Entry<String, List<BigDecimal>> entry ->
            List<BigDecimal> yDataList = entry.value
            xLabels.eachWithIndex { String xLabel, int i ->
                dataset.setValue(i < yDataList.size() ? yDataList[i] : 0, entry.key, xLabel)
            }
        }
        chart = ChartFactory.createBarChart(
                diagramTitle,
                xTitle,
                yTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        )
        CategoryItemRenderer renderer = isStacked ? new GroupedStackedBarRenderer() : new BarRenderer()
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator())
        renderer.setDefaultItemLabelsVisible(true)
        renderer.setMaximumBarWidth(0.1 as double)
        renderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER))

        CategoryPlot plot = (CategoryPlot) chart.getPlot()
        NumberAxis yAxis= (NumberAxis) plot.getRangeAxis()
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits())
        plot.setRenderer(renderer)
    }

    @Override
    void visitLineDiagram(List<String> xLabels, String title) {
        this.xLabels = xLabels
        this.diagramTitle = title
        this.yDataPerKey = [:]
    }

    @Override
    void visitLineDiagramEnd(String xTitle, String yTitle) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset()
        yDataPerKey.each { Map.Entry<String, List<BigDecimal>> entry ->
            List<BigDecimal> yDataList = entry.value
            xLabels.eachWithIndex { String xLabel, int i ->
                dataset.setValue(i < yDataList.size() ? yDataList[i] : 0, entry.key, xLabel)
            }
        }
        chart = ChartFactory.createLineChart(
                diagramTitle,
                xTitle,
                yTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        )
        LineAndShapeRenderer renderer = new LineAndShapeRenderer()
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator())
        renderer.setDefaultItemLabelsVisible(true)

        CategoryPlot plot = (CategoryPlot) chart.getPlot()
        NumberAxis yAxis= (NumberAxis) plot.getRangeAxis()
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits())
        plot.setRenderer(renderer)
    }

    @Override
    void visitPieDiagram(String title) {
        this.xLabels = []
        this.diagramTitle = title
        this.yDataPerKey = [:]
    }

    @Override
    void visitPieDiagramEnd() {
        DefaultPieDataset dataset = new DefaultPieDataset()
        BigDecimal total = yDataPerKey.values().collect { it.size() ? it.first() : 0 }.sum() as BigDecimal
        yDataPerKey.each {
            BigDecimal value = it.value.size() ? it.value.first() : 0
            dataset.setValue("${it.key}: ${value} (${(value / total * 100).round(2)}%)", value)
        }
        dataset.sortByValues(SortOrder.DESCENDING)
        chart = ChartFactory.createPieChart(
                diagramTitle,
                dataset,
                true,
                true,
                false
        )
        PiePlot plot = (PiePlot) chart.getPlot()
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10))
        plot.setNoDataMessage("No data available")
    }

    @Override
    void dataset(String key, List<BigDecimal> data) {
        yDataPerKey.put(key, data)
    }
}
