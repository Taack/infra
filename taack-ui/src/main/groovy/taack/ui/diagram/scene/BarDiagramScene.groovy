package taack.ui.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.diagram.render.IDiagramRender

import java.awt.Color

@CompileStatic
class BarDiagramScene {
    final private IDiagramRender render
    final private List<String> xLabels
    final private Map<String, List<BigDecimal>> yDataPerKey
    final private boolean isStacked

    final private BigDecimal width
    final private BigDecimal height

    BarDiagramScene(IDiagramRender render, List<String> xLabels, Map<String, List<BigDecimal>> yDataPerKey, boolean isStacked) {
        this.render = render
        this.xLabels = xLabels
        this.yDataPerKey = yDataPerKey
        this.isStacked = isStacked

        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
    }

    enum BarColor {
        RED(new Color(255, 99, 132)),
        ORANGE(new Color(255, 159, 64)),
        YELLOW(new Color(255, 205, 86)),
        GREEN(new Color(75, 192, 192)),
        PURPLE(new Color(153, 102, 255)),
        BLUE(new Color(54, 162, 235)),
        GREY(new Color(201, 203, 207))

        BarColor(Color color) {
            this.color = color
        }

        final Color color

        static Color colorFrom(int i) {
            values()[i % values().size()].color
        }
    }

    BigDecimal LEGEND_MARGIN = 10.0
    BigDecimal LEGEND_RECT_WIDTH = 40.0
    BigDecimal LEGEND_RECT_HEIGHT = 13.0 // same as font size
    BigDecimal DIAGRAM_MARGIN_LEFT = 60.0
    BigDecimal DIAGRAM_MARGIN_RIGHT = 20.0
    BigDecimal DIAGRAM_MARGIN_TOP = 10.0
    BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0
    BigDecimal MAX_BAR_WIDTH = 200.0

    void draw() {
        Set<String> keys = yDataPerKey.keySet()
        if (xLabels.isEmpty() || keys.isEmpty()) {
            return
        }

        // legend
        BigDecimal legendX = (width - keys.size() * (LEGEND_RECT_WIDTH + 5.0 + LEGEND_MARGIN) + LEGEND_MARGIN - render.measureText(keys.join(""))) / 2
        keys.eachWithIndex { String key, int index ->
            // rect
            render.translateTo(legendX, LEGEND_MARGIN)
            Color rectColor = BarColor.colorFrom(index)
            render.fillStyle(new Color(rectColor.red, rectColor.green, rectColor.blue, 128))
            render.renderRect(LEGEND_RECT_WIDTH, LEGEND_RECT_HEIGHT, IDiagramRender.RectStyle.fill)
            render.fillStyle(rectColor)
            render.renderRect(LEGEND_RECT_WIDTH, LEGEND_RECT_HEIGHT, IDiagramRender.RectStyle.stroke)

            // text
            render.translateTo(legendX + LEGEND_RECT_WIDTH + 5.0, LEGEND_MARGIN)
            render.renderLabel(key)

            legendX += LEGEND_RECT_WIDTH + 5.0 + render.measureText(key) + LEGEND_MARGIN
        }

        // y axis
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
        BigDecimal startLabelY = values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
        BigDecimal totalGapY = values.last() - startLabelY
        BigDecimal gapY
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
        BigDecimal diagramTopMargin = LEGEND_MARGIN * 2 + LEGEND_RECT_HEIGHT + DIAGRAM_MARGIN_TOP
        BigDecimal gapHeight = (height - diagramTopMargin - DIAGRAM_MARGIN_BOTTOM) / gapNumberY
        render.fillStyle(new Color(231, 231, 231))
        for (int i = 0; i <= gapNumberY; i++) {
            // background horizontal line
            render.translateTo(DIAGRAM_MARGIN_LEFT - 5.0, diagramTopMargin + gapHeight * i)
            render.renderLine(width - (DIAGRAM_MARGIN_LEFT - 5.0) - DIAGRAM_MARGIN_RIGHT, 0.0)

            // y axis label
            String yLabel = "${gapY < 1 ? (endLabelY - gapY * i).round(1) : (endLabelY - gapY * i).toInteger()}"
            render.translateTo(DIAGRAM_MARGIN_LEFT - 10.0 - render.measureText(yLabel), diagramTopMargin + gapHeight * i - 6.5)
            render.renderLabel(yLabel)
        }

        // x axis
        int gapNumberX = xLabels.size()
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
        boolean rotateLabel = false
        xLabels.each {
            if (render.measureText(it) >= gapWidth * 0.8) {
                rotateLabel = true
            }
        }
        for (int i = 0; i < gapNumberX; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i

            // background vertical line
            render.translateTo(startX, diagramTopMargin)
            render.fillStyle(new Color(231, 231, 231))
            render.renderLine(0.0, height - diagramTopMargin - (DIAGRAM_MARGIN_BOTTOM - 5.0))

            // x axis label
            String xLabel = xLabels[i]
            if (rotateLabel) {
                render.translateTo(startX + gapWidth * 0.5 - render.measureText(xLabel), height - DIAGRAM_MARGIN_BOTTOM + 10.0)
                render.renderRotatedLabel(xLabel, -15.0, startX + gapWidth * 0.5, height - DIAGRAM_MARGIN_BOTTOM + 10.0)
            } else {
                render.translateTo(startX + (gapWidth - render.measureText(xLabel)) / 2, height - DIAGRAM_MARGIN_BOTTOM + 10.0)
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
                    Color rectColor = BarColor.colorFrom(j)
                    render.fillStyle(new Color(rectColor.red, rectColor.green, rectColor.blue, 128))
                    render.renderRect(barWidth, barHeight, IDiagramRender.RectStyle.fill)
                    render.fillStyle(rectColor)
                    render.renderRect(barWidth, barHeight, IDiagramRender.RectStyle.stroke)

                    // data label
                    String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
                    render.translateTo(barX + (barWidth - render.measureText(yDataLabel)) / 2, isStacked ? barY - barHeight / 2 - 6.5 : barY - barHeight - 15.0)
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
}