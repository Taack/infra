package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

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
                render.renderGroup(["element-type": ElementType.DATA_GROUP])
                Map<Object, BigDecimal> pointList = dataPerKey[keys[i]]
                List<Number> xList = pointList.keySet().sort() as List<Number>
                KeyColor circleColor = KeyColor.colorFrom(i)
                for (int j = 0; j < xList.size() - 1; j++) {
                    render.renderGroup(["element-type": ElementType.DATA, dataset: keys[i]])

                    // line to next circle
                    Number x = xList[j]
                    Number y = pointList[x]
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + (x - minX) / (maxX - minX) * totalWidth
                    Number nextX = xList[j + 1]
                    Number nextY = pointList[nextX]
                    BigDecimal nextYHeight = (nextY - startLabelY) / gapY * gapHeight
                    BigDecimal nextXWidth = DIAGRAM_MARGIN_LEFT + (nextX - minX) / (maxX - minX) * totalWidth
                    render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                    render.fillStyle(circleColor.color)
                    render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)

                    render.renderGroupEnd()
                }
                render.renderGroupEnd()
            }
        } else { // discrete
            Map<String, List<BigDecimal>> yDataListPerKey = [:]
            for (int i = 0; i < keys.size(); i++) {
                String key = keys[i]
                yDataListPerKey.put(key, dataPerKey[key].values() as List<BigDecimal>)
            }

            BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / (xLabelList.size() > 1 ? xLabelList.size() - 1 : 1)
            for (int i = 0; i < xLabelList.size() - 1; i++) {
                render.renderGroup(["element-type": ElementType.DATA_GROUP])
                BigDecimal xWidth = gapWidth * i
                for (int j = 0; j < keys.size(); j++) {
                    render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j]])

                    // line to next circle
                    List<BigDecimal> yList = yDataListPerKey[keys[j]]
                    BigDecimal y = i < yList.size() ? yList[i] : 0.0
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    BigDecimal yHeight2 = ((i + 1 < yList.size() ? yList[i + 1] : 0.0) - startLabelY) / gapY * gapHeight
                    BigDecimal xWidth2 = gapWidth * (i + 1)
                    render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                    render.fillStyle(KeyColor.colorFrom(j).color)
                    render.renderLine(xWidth2 - xWidth, yHeight - yHeight2)

                    render.renderGroupEnd()
                }
                render.renderGroupEnd()
            }
        }
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        buildScrollStart()
        drawVerticalBackground(false)
        drawDataLine()
        drawDataPoint()
        buildScrollEnd()
    }
}