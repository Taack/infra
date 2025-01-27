package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class ScatterDiagramScene extends RectBackgroundDiagramScene {
    final private List<String> pointImageHref
    protected BigDecimal dataPointRadius

    ScatterDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, String... pointImageHref) {
        super(render, dataPerKey, true)
        this.pointImageHref = pointImageHref.toList()
        this.dataPointRadius = LEGEND_IMAGE_WIDTH / 2
    }

    void drawDataPoint() {
        Set<String> keys = dataPerKey.keySet()
        if (xLabelList.every { it instanceof Number }) { // continuous
            Integer minX = xLabelList.first() as Integer
            Integer maxX = xLabelList.last() as Integer
            BigDecimal totalWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
            for (int i = 0; i < keys.size(); i++) {
                render.renderGroup(["element-type": ElementType.DATA_GROUP])
                Map<Object, BigDecimal> pointList = dataPerKey[keys[i]]
                List<Number> xList = pointList.keySet().sort() as List<Number>
                for (int j = 0; j < xList.size(); j++) {
                    Number x = xList[j]
                    Number y = pointList[x]
                    String xLabel = x.toDouble() % 1 == 0 ? "${x.toInteger()}" : "$x"
                    String yLabel = y.toDouble() % 1 == 0 ? "${y.toInteger()}" : "$y"
                    render.renderGroup(["element-type": ElementType.DATA, dataset: keys[i], "data-label": "($xLabel, $yLabel)", style: "pointer-events: bounding-box;"])

                    BigDecimal xWidth = (x - minX) / (maxX - minX) * totalWidth
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    KeyColor circleColor = KeyColor.colorFrom(i)
                    render.fillStyle(circleColor.color)
                    // data point
                    if (dataPointRadius > 0) {
                        if (i < pointImageHref.size()) {
                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth - dataPointRadius, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                            render.renderImage(pointImageHref[i], dataPointRadius * 2, dataPointRadius * 2)
                        } else {
                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                        }
                    }
//                    // data label
//                    if (y > startLabelY) {
//                        String xLabel = x.toDouble() % 1 == 0 ? "${x.toInteger()}" : "$x"
//                        String yLabel = y.toDouble() % 1 == 0 ? "${y.toInteger()}" : "$y"
//                        String dataLabel = "($xLabel, $yLabel)"
//                        if (dataPointRadius > 5) { // put label at right
//                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth + dataPointRadius + 2.0, height - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
//                        } else { // put label at top
//                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth - render.measureText(dataLabel) / 2, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
//                        }
//                        render.renderLabel(dataLabel)
//                    }
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
            for (int i = 0; i < xLabelList.size(); i++) {
                render.renderGroup(["element-type": ElementType.DATA_GROUP])
                BigDecimal xWidth = gapWidth * i
                for (int j = 0; j < keys.size(); j++) {
                    List<BigDecimal> yList = yDataListPerKey[keys[j]]
                    BigDecimal y = i < yList.size() ? yList[i] : 0.0
                    render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "data-label": "${xLabelList[i]}: ${y.toDouble() % 1 == 0 ? "${y.toInteger()}" : "$y"}", style: "pointer-events: bounding-box;"])

                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    // data point
                    if (dataPointRadius > 0) {
                        if (j < pointImageHref.size()) {
                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth - dataPointRadius, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                            render.renderImage(pointImageHref[j], dataPointRadius * 2, dataPointRadius * 2)
                        } else {
                            KeyColor circleColor = KeyColor.colorFrom(j)
                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.fillStyle(circleColor.color)
                            render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                        }
                    }
//                    // data label
//                    if (y > startLabelY) {
//                        String yDataLabel = y.toDouble() % 1 == 0 ? "${y.toInteger()}" : "$y"
//                        if (dataPointRadius > 5) { // put label at right
//                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth + dataPointRadius + 2.0, height - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
//                        } else { // put label at top
//                            render.translateTo(DIAGRAM_MARGIN_LEFT + xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
//                        }
//                        render.renderLabel(yDataLabel)
//                    }
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
        drawLegend(pointImageHref)
        drawHorizontalBackground()
        drawVerticalBackground(false)
        drawDataPoint()
    }
}