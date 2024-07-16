package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
class LineDiagramScene extends DiagramScene {
    final private Set<Object> xLabelList

    private BigDecimal startLabelY
    private BigDecimal gapY
    private BigDecimal gapHeight
    final private BigDecimal CIRCLE_RADIUS = 2.5
    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0
    final private BigDecimal MIN_GAP_WIDTH = 5.0 // hide dataLabel/dataCircle/backgroundVerticalLine when gap too narrow (it means huge number of labels)

    final private Integer GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS = 5 // todo: depending on diagram width ?

    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        this.fontSize = render.getFontSize()
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.dataPerKey = dataPerKey

        this.xLabelList = []
        Set xDataList = dataPerKey.collect { it.value.keySet() }.flatten() as Set
        if (!xDataList.isEmpty()) {
            if (xDataList.every { it instanceof Number }) {
                if (xDataList.size() == 1) {
                    Integer value = xDataList.first() as Integer
                    this.xLabelList.add(value - 1)
                    this.xLabelList.add(value)
                    this.xLabelList.add(value + 1)
                } else {
                    Double min = Math.floor(xDataList.sort().first() as Double)
                    Double max = Math.ceil(xDataList.sort().last() as Double)
                    Double gap = Math.ceil(((max - min) / GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS) as Double)
                    for (int i = 0; i <= GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS; i++) {
                        this.xLabelList.add((min + gap * i).toInteger())
                    }
                }
            } else {
                this.xLabelList.addAll(dataPerKey[dataPerKey.keySet().first()].keySet().collect { it.toString() })
            }
        }
    }

    void drawHorizontalBackground() {
        Set<BigDecimal> values = dataPerKey.collect { it.value.values() }.flatten().sort() as Set<BigDecimal>
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
            render.translateTo(DIAGRAM_MARGIN_LEFT - AXIS_LABEL_MARGIN - render.measureText(yLabel), diagramMarginTop + gapHeight * i - fontSize / 2)
            render.renderLabel(yLabel)
        }
    }

    void drawVerticalBackground() {
        int gapNumberX = xLabelList.size() - 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
        int showLabelEveryX = (render.measureText(xLabelList.join("")) / (gapWidth * gapNumberX * 0.8)).toInteger()
        for (int i = 0; i < gapNumberX + 1; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i

            // background vertical line
            if (gapWidth >= MIN_GAP_WIDTH || i % showLabelEveryX == 0) {
                render.translateTo(startX, diagramMarginTop)
                render.fillStyle(new Color(231, 231, 231))
                render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
            }

            // x axis label
            String xLabel = xLabelList[i]
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
        Set<String> keys = dataPerKey.keySet()
        if (xLabelList.every { it instanceof Number }) { // continuous
            Integer minX = xLabelList.first() as Integer
            Integer maxX = xLabelList.last() as Integer
            BigDecimal totalWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
            for (int i = 0; i < keys.size(); i++) {
                Map<Object, BigDecimal> pointList = dataPerKey[keys[i]]
                List<Number> xList = pointList.keySet().sort() as List<Number>
                Color circleColor = LegendColor.colorFrom(i)
                for (int j = 0; j < xList.size(); j++) {
                    Number x = xList[j]
                    Number y = pointList[x]
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + (x - minX) / (maxX - minX) * totalWidth

                    // circle
                    render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                    render.fillStyle(circleColor)
                    render.renderCircle(CIRCLE_RADIUS, IDiagramRender.DiagramStyle.stroke)

                    // line to next circle
                    if (j < pointList.size() - 1) { // not the last point
                        Number nextX = xList[j + 1]
                        Number nextY = pointList[nextX]
                        BigDecimal nextYHeight = (nextY - startLabelY) / gapY * gapHeight
                        BigDecimal nextXWidth = DIAGRAM_MARGIN_LEFT + (nextX - minX) / (maxX - minX) * totalWidth
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                        render.fillStyle(new Color(circleColor.red, circleColor.green, circleColor.blue, 192))
                        render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)
                    }

                    // data label
                    if (y > startLabelY) {
                        String xLabel = x.toDouble() % 1 == 0 ? "${x.toInteger()}" : "$x"
                        String yLabel = y.toDouble() % 1 == 0 ? "${y.toInteger()}" : "$y"
                        String dataLabel = "($xLabel, $yLabel)"
                        render.translateTo(xWidth - render.measureText(dataLabel) / 2, height - DIAGRAM_MARGIN_BOTTOM - yHeight - CIRCLE_RADIUS - fontSize - 2.0)
                        render.renderLabel(dataLabel)
                    }
                }
            }
        } else { // discrete
            Map<String, List<BigDecimal>> yDataListPerKey = [:]
            for (int i = 0; i < keys.size(); i++) {
                String key = keys[i]
                yDataListPerKey.put(key, dataPerKey[key].values() as List<BigDecimal>)
            }

            int gapNumberX = xLabelList.size() - 1
            BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
            for (int i = 0; i < gapNumberX + 1; i++) {
                BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i
                for (int j = 0; j < keys.size(); j++) {
                    BigDecimal yData = yDataListPerKey[keys[j]][i]
                    BigDecimal lineHeight = (yData - startLabelY) / gapY * gapHeight
                    Color circleColor = LegendColor.colorFrom(j)

                    // circle
                    if (gapWidth > MIN_GAP_WIDTH || i == 0) {
                        render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight)
                        render.fillStyle(circleColor)
                        render.renderCircle(CIRCLE_RADIUS, IDiagramRender.DiagramStyle.stroke)
                    }

                    // line to next circle
                    if (i < gapNumberX) { // not the last point
                        BigDecimal lineHeight2 = (yDataListPerKey[keys[j]][i + 1] - startLabelY) / gapY * gapHeight
                        BigDecimal startX2 = DIAGRAM_MARGIN_LEFT + gapWidth * (i + 1)
                        render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight)
                        render.fillStyle(new Color(circleColor.red, circleColor.green, circleColor.blue, 192))
                        render.renderLine(startX2 - startX, lineHeight - lineHeight2)
                    }

                    // data label
                    if (yData > startLabelY && gapWidth > MIN_GAP_WIDTH) {
                        String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
                        render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight - CIRCLE_RADIUS - fontSize - 2.0)
                        render.renderLabel(yDataLabel)
                    }
                }
            }
        }
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        drawVerticalBackground()
        drawDataLine()
    }
}