package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.text.SimpleDateFormat

@CompileStatic
class ScatterDiagramScene extends RectBackgroundDiagramScene {
    final private List<String> pointImageHref
    protected BigDecimal dataPointRadius

    ScatterDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption, List<String> pointImageHref = []) {
        super(render, dataPerKey, diagramOption)
        this.pointImageHref = pointImageHref
        this.dataPointRadius = LEGEND_IMAGE_WIDTH / 2
    }

    static String objectToString(Object o) {
        return o instanceof Date ? new SimpleDateFormat('yyyy-MM-dd HH:mm').format(o) : o instanceof Number ? numberToString(o.toBigDecimal()) : o.toString()
    }

    void drawDataPoint(Boolean hasLineBetweenPoints) {
        Set<String> keys = dataPerKey.keySet()
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / (xLabelList.size() > 1 ? xLabelList.size() - 1 : 1)
        if (xLabelList.every { it instanceof Number } || xLabelList.every { it instanceof Date }) { // continuous
            BigDecimal minX = objectToNumber(xLabelList.first())
            BigDecimal maxX = objectToNumber(xLabelList.last())
            BigDecimal totalWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
            for (int i = 0; i < keys.size(); i++) {
                Map<Object, BigDecimal> pointList = dataPerKey[keys[i]]
                List<Object> xList = pointList.keySet().sort() as List<Object>
                for (int j = 0; j < xList.size(); j++) {
                    BigDecimal x = objectToNumber(xList[j])
                    BigDecimal y = pointList[xList[j]]
                    BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + (x - minX) / (maxX - minX) * totalWidth
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    String xLabel = objectToString(xList[j])
                    String yLabel = numberToString(y)
                    String dataLabel = xList[j] instanceof Date ? "${xLabel}: ${yLabel}" : "($xLabel, $yLabel)"
                    KeyColor circleColor = KeyColor.colorFrom(i)
                    render.fillStyle(circleColor.color)

                    // data point
                    if (dataPointRadius > 0 && (!hasLineBetweenPoints || alwaysShowFullInfo || gapWidth >= MIN_GAP_WIDTH)) {
                        render.renderGroup(['element-type': ElementType.DATA,
                                            dataset: keys[i],
                                            'data-x': xLabel,
                                            'data-y': yLabel,
                                            'data-label': dataLabel,
                                            style: 'pointer-events: bounding-box;'])
                        if (i < pointImageHref.size()) {
                            render.translateTo(xWidth - dataPointRadius, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                            render.renderImage(pointImageHref[i], dataPointRadius * 2, dataPointRadius * 2)
                        } else {
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                        }
                        render.renderGroupEnd()
                    }
                    // line to next circle
                    if (hasLineBetweenPoints && j < xList.size() - 1) {
                        render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i]])
                        BigDecimal nextX = objectToNumber(xList[j + 1])
                        BigDecimal nextY = pointList[xList[j + 1]]
                        BigDecimal nextXWidth = DIAGRAM_MARGIN_LEFT + (nextX - minX) / (maxX - minX) * totalWidth
                        BigDecimal nextYHeight = (nextY - startLabelY) / gapY * gapHeight
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                        render.fillStyle(circleColor.color)
                        render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)
                        render.renderGroupEnd()
                    }
                    if (diagramOption?.showDataCount && gapWidth >= MIN_GAP_WIDTH) {
                        // data label
                        if (dataPointRadius > 5) { // put label at right
                            render.translateTo(xWidth + dataPointRadius + 2.0, height - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
                        } else { // put label at top
                            render.translateTo(xWidth - render.measureText(dataLabel) / 2, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
                        }
                        render.renderLabel(dataLabel)
                    }
                }
            }
        } else { // discrete
            for (int i = 0; i < xLabelList.size(); i++) {
                BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + gapWidth * i
                for (int j = 0; j < keys.size(); j++) {
                    List<BigDecimal> yList = dataPerKey[keys[j]].values() as List<BigDecimal>
                    BigDecimal y = i < yList.size() ? yList[i] : 0.0
                    String yDataLabel = numberToString(y)
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight

                    // data point
                    if (dataPointRadius > 0 && (!hasLineBetweenPoints || alwaysShowFullInfo || gapWidth >= MIN_GAP_WIDTH)) {
                        render.renderGroup(['element-type': ElementType.DATA,
                                            dataset: keys[j],
                                            'data-x': xLabelList[i],
                                            'data-y': yDataLabel,
                                            'data-label': "${xLabelList[i]}: ${yDataLabel}",
                                            style: 'pointer-events: bounding-box;'])
                        if (j < pointImageHref.size()) {
                            render.translateTo(xWidth - dataPointRadius, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                            render.renderImage(pointImageHref[j], dataPointRadius * 2, dataPointRadius * 2)
                        } else {
                            KeyColor circleColor = KeyColor.colorFrom(j)
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.fillStyle(circleColor.color)
                            render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                        }
                        render.renderGroupEnd()
                    }
                    // line to next circle
                    if (hasLineBetweenPoints && i < xLabelList.size() - 1) {
                        render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j]])
                        BigDecimal nextYHeight = ((i + 1 < yList.size() ? yList[i + 1] : 0.0) - startLabelY) / gapY * gapHeight
                        BigDecimal nextXWidth = DIAGRAM_MARGIN_LEFT + gapWidth * (i + 1)
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight)
                        render.fillStyle(KeyColor.colorFrom(j).color)
                        render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)
                        render.renderGroupEnd()
                    }
                    if (diagramOption?.showDataCount && gapWidth >= MIN_GAP_WIDTH) {
                        // data label
                        if (y > startLabelY) {
                            if (dataPointRadius > 5) { // put label at right
                                render.translateTo(xWidth + dataPointRadius + 2.0, height - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
                            } else { // put label at top
                                render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
                            }
                            render.renderLabel(yDataLabel)
                        }
                    }
                }
            }
        }
    }

    void draw(boolean alwaysShowFullInfo = false) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend(pointImageHref)
        drawHorizontalBackground()
        buildTransformAreaStart('scatter')
        drawVerticalBackground()
        drawDataPoint(false)
        buildTransformAreaEnd()
    }
}