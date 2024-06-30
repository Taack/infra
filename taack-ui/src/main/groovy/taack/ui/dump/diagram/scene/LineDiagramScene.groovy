package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
class LineDiagramScene extends DiagramScene {
    final private List<String> xLabels

    private BigDecimal startLabelY
    private BigDecimal gapY
    private BigDecimal gapHeight
    final private BigDecimal CIRCLE_RADIUS = 2.5
    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0
    final private BigDecimal MIN_GAP_WIDTH = 5.0 // hide dataLabel/dataCircle/backgroundVerticalLine when gap too narrow (it means huge number of labels)

    LineDiagramScene(IDiagramRender render, List<String> xLabels, Map<String, List<BigDecimal>> yDataPerKey) {
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.yDataPerKey = yDataPerKey
        this.xLabels = xLabels
    }

    void drawHorizontalBackground() {
        Set<BigDecimal> values = yDataPerKey.values().flatten().sort() as Set<BigDecimal>
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

    void drawVerticalBackground() {
        int gapNumberX = xLabels.size() - 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
        int showLabelEveryX = (render.measureText(xLabels.join("")) / (gapWidth * gapNumberX * 0.8)).toInteger()
        for (int i = 0; i < gapNumberX + 1; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i

            // background vertical line
            if (gapWidth >= MIN_GAP_WIDTH || i % showLabelEveryX == 0) {
                render.translateTo(startX, diagramMarginTop)
                render.fillStyle(new Color(231, 231, 231))
                render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
            }

            // x axis label
            String xLabel = xLabels[i]
            if (showLabelEveryX >= 1) {
                if (i % showLabelEveryX == 0) {
                    render.translateTo(startX - render.measureText(xLabel), height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, startX, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                }
            } else {
                render.translateTo(startX - render.measureText(xLabel) / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                render.renderLabel(xLabel)
            }
        }
    }

    void drawDataLine() {
        Set<String> keys = yDataPerKey.keySet()
        int gapNumberX = xLabels.size() - 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
        boolean hideInfo = gapWidth < MIN_GAP_WIDTH
        for (int i = 0; i < gapNumberX + 1; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i
            for (int j = 0; j < keys.size(); j++) {
                BigDecimal yData = yDataPerKey[keys[j]][i]
                BigDecimal lineHeight = (yData - startLabelY) / gapY * gapHeight
                Color circleColor = LegendColor.colorFrom(j)

                // circle
                if (!hideInfo || i == 0) {
                    render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight)
                    render.fillStyle(circleColor)
                    render.renderCircle(CIRCLE_RADIUS, IDiagramRender.DiagramStyle.stroke)
                }

                // line to next circle
                if (i < gapNumberX) { // not the last point
                    BigDecimal lineHeight2 = (yDataPerKey[keys[j]][i + 1] - startLabelY) / gapY * gapHeight
                    BigDecimal startX2 = DIAGRAM_MARGIN_LEFT + gapWidth * (i + 1)
                    render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight)
                    render.fillStyle(new Color(circleColor.red, circleColor.green, circleColor.blue, 192))
                    render.renderLine(startX2 - startX, lineHeight - lineHeight2)
                }

                // data label
                if (yData > startLabelY && !hideInfo) {
                    String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
                    render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight - CIRCLE_RADIUS - FONT_SIZE - 2.0)
                    render.renderLabel(yDataLabel)
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
        drawVerticalBackground()
        drawDataLine()
    }
}