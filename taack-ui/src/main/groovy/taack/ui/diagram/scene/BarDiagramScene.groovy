package taack.ui.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.diagram.render.IDiagramRender

import java.awt.Color

@CompileStatic
class BarDiagramScene extends DiagramScene {
    final private List<String> xLabels
    final private boolean isStacked

    private BigDecimal startLabelY
    private BigDecimal gapY
    private BigDecimal gapHeight
    final private BigDecimal MAX_BAR_WIDTH = 200.0
    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0

    BarDiagramScene(IDiagramRender render, List<String> xLabels, Map<String, List<BigDecimal>> yDataPerKey, boolean isStacked) {
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.yDataPerKey = yDataPerKey
        this.xLabels = xLabels
        this.isStacked = isStacked
    }

    void drawHorizontalBackground() {
        Set<String> keys = yDataPerKey.keySet()
        Set<BigDecimal> values
        if (isStacked) {
            values = []
            for (int i = 0; i < xLabels.size(); i++) {
                BigDecimal value = 0.0
                for (int j = 0; j < keys.size(); j++) {
                    value += yDataPerKey[keys[j]][i]
                }
                values.add(value)
            }
            values = values.sort() as Set<BigDecimal>
        } else {
            values = yDataPerKey.values().flatten().sort() as Set<BigDecimal>
        }
        startLabelY = values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
        BigDecimal totalGapY = values.last() - startLabelY
        int gapNumberY
        if (totalGapY <= 1) {
            gapY = 0.2
            gapNumberY = 5
        } else if (totalGapY <= 5) {
            gapY = 1.0
            gapNumberY = 5
        } else if (totalGapY <= 10) {
            gapY = 1.0
            gapNumberY = 10
        } else {
            gapY = Math.ceil((totalGapY / 10).toDouble()).toBigDecimal()
            gapNumberY = 10
        }
        BigDecimal endLabelY = startLabelY + gapY * gapNumberY
        gapHeight = (height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM) / gapNumberY
        render.fillStyle(new Color(231, 231, 231))
        for (int i = 0; i <= gapNumberY; i++) {
            // background horizontal line
            render.translateTo(DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM, diagramMarginTop + gapHeight * i)
            render.renderLine(width - (DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM) - DIAGRAM_MARGIN_RIGHT, 0.0)

            // y axis label
            String yLabel = "${gapY < 1 ? (endLabelY - gapY * i).round(1) : (endLabelY - gapY * i).toInteger()}"
            render.translateTo(DIAGRAM_MARGIN_LEFT - AXIS_LABEL_MARGIN - render.measureText(yLabel), diagramMarginTop + gapHeight * i - FONT_SIZE / 2)
            render.renderLabel(yLabel)
        }
    }

    void drawVerticalBackgroundAndDataBar() {
        Set<String> keys = yDataPerKey.keySet()
        int gapNumberX = xLabels.size()
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
        int showLabelEveryX = (render.measureText(xLabels.join("")) / (gapWidth * gapNumberX * 0.8)).toInteger()
        for (int i = 0; i < gapNumberX; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i

            // background vertical line
            render.translateTo(startX, diagramMarginTop)
            render.fillStyle(new Color(231, 231, 231))
            render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))

            // x axis label
            String xLabel = xLabels[i]
            if (showLabelEveryX >= 1) {
                if (i % showLabelEveryX == 0) {
                    render.translateTo(startX + gapWidth * 0.5 - render.measureText(xLabel), height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, startX + gapWidth * 0.5, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                }
            } else {
                render.translateTo(startX + (gapWidth - render.measureText(xLabel)) / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                render.renderLabel(xLabel)
            }

            // data bar
            int barNumber = isStacked ? 1 : keys.size()
            BigDecimal horizontalPadding = gapWidth * 0.2
            BigDecimal barWidth = barNumber > 1 ? (gapWidth - horizontalPadding) * 0.8 / barNumber : (gapWidth - horizontalPadding)
            BigDecimal barMargin = barNumber > 1 ? (gapWidth - horizontalPadding) * 0.2 / (barNumber - 1) : 0.0
            if (barWidth > MAX_BAR_WIDTH) {
                barWidth = MAX_BAR_WIDTH
                horizontalPadding = gapWidth - barWidth * barNumber - barMargin * (barNumber - 1)
            }
            BigDecimal barX = startX + horizontalPadding / 2
            BigDecimal barY = height - DIAGRAM_MARGIN_BOTTOM
            for (int j = 0; j < keys.size(); j++) {
                BigDecimal yData = yDataPerKey[keys[j]][i]
                BigDecimal barHeight = (yData - startLabelY) / gapY * gapHeight
                if (yData > startLabelY) {
                    // bar rect
                    render.translateTo(barX, barY - barHeight)
                    Color rectColor = LegendColor.colorFrom(j)
                    render.fillStyle(new Color(rectColor.red, rectColor.green, rectColor.blue, 128))
                    render.renderRect(barWidth, barHeight, IDiagramRender.DiagramStyle.fill)
                    render.fillStyle(rectColor)
                    render.renderRect(barWidth, barHeight, IDiagramRender.DiagramStyle.stroke)

                    // data label
                    String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
                    render.translateTo(barX + (barWidth - render.measureText(yDataLabel)) / 2, isStacked ? barY - barHeight / 2 - FONT_SIZE / 2 : barY - barHeight - FONT_SIZE - 2.0)
                    render.renderLabel(yDataLabel)
                }

                if (isStacked) {
                    barY -= barHeight
                } else {
                    barX += barWidth + barMargin
                }
            }
        }
    }

    void draw() {
        if (xLabels.isEmpty() || yDataPerKey.keySet().isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        drawVerticalBackgroundAndDataBar()
    }
}