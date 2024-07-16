package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        super(render, dataPerKey)
        this.dataPointRadius = 2.5
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

                    // line to next circle
                    if (i < gapNumberX) { // not the last point
                        BigDecimal lineHeight2 = (yDataListPerKey[keys[j]][i + 1] - startLabelY) / gapY * gapHeight
                        BigDecimal startX2 = DIAGRAM_MARGIN_LEFT + gapWidth * (i + 1)
                        render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight)
                        render.fillStyle(new Color(circleColor.red, circleColor.green, circleColor.blue, 192))
                        render.renderLine(startX2 - startX, lineHeight - lineHeight2)
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
        drawDataPoint()
    }
}