package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class ScatterDiagramScene extends RectBackgroundDiagramScene {
    final private List<String> pointImageHref
    protected BigDecimal dataPointRadius
    protected BigDecimal dataPointClickableRadius

    ScatterDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption, List<String> pointImageHref = []) {
        super(render, dataPerKey, diagramOption)
        this.pointImageHref = pointImageHref
        this.dataPointRadius = LEGEND_IMAGE_WIDTH / 2
        this.dataPointClickableRadius = dataPointRadius
    }

    void drawDataPoint(Boolean hasLineBetweenPoints) {
        Set<String> keys = dataPerKey.keySet()
        if (!isXLabelInsideGap) { // continuous
            BigDecimal minX = objectToNumber(xLabelList.first())
            BigDecimal maxX = objectToNumber(xLabelList.last())
            BigDecimal totalWidth = render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight
            BigDecimal gapWidth = totalWidth / (xLabelList.size() > 1 ? xLabelList.size() - 1 : 1)
            for (int i = 0; i < keys.size(); i++) {
                Map<Object, BigDecimal> pointList = dataPerKey[keys[i]]
                List<Object> xList = pointList.keySet().sort() as List<Object>
                for (int j = 0; j < xList.size(); j++) {
                    Object xData = xList[j]
                    BigDecimal x = objectToNumber(xData)
                    BigDecimal y = pointList[xData]
                    BigDecimal xWidth = diagramMarginLeft + (x - minX) / (maxX - minX) * totalWidth
                    BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                    String xLabel = xData instanceof Date ? diagramOption.xLabelDateFormat.detailFormat(xData) : xData instanceof Number ? numberToString(xData.toBigDecimal()) : xData.toString()
                    String yLabel = numberToString(y)
                    String dataLabel = xData instanceof Date ? "${xLabel} : ${yLabel}" : "($xLabel, $yLabel)"
                    Color keyColor = getKeyColor(i)
                    render.fillStyle(keyColor)

                    // data point
                    if (dataPointRadius > 0 && (!hasLineBetweenPoints || alwaysShowFullInfo || gapWidth >= MIN_GAP_WIDTH)) {
                        render.renderGroup(['element-type': ElementType.TOOLTIP,
                                            'key-label': keys[i],
                                            'key-color': KeyColor.colorToString(keyColor),
                                            'key-description': dataLabel,
                                            'diagram-action-url': diagramOption?.clickActionUrl ?: '',
                                            'data-x': xLabel,
                                            'data-y': yLabel])
                        render.renderGroup(['element-type': ElementType.DATA,
                                            dataset: keys[i],
                                            style: 'pointer-events: bounding-box;'])
                        if (i < pointImageHref.size()) {
                            render.translateTo(xWidth - dataPointRadius, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                            render.renderImage(pointImageHref[i], dataPointRadius * 2, dataPointRadius * 2)
                        } else {
                            render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                        }
                        if (dataPointClickableRadius > dataPointRadius) { // transport circle to enlarge clickable area
                            render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.fillStyle(new Color(0, 0, 0, 0))
                            render.renderCircle(dataPointClickableRadius, IDiagramRender.DiagramStyle.fill)
                        }
                        render.renderGroupEnd()
                        render.renderGroupEnd()
                    }
                    // data label
                    if (diagramOption?.showDataCount && gapWidth >= MIN_GAP_WIDTH) {
                        if (hasLineBetweenPoints) { // line: put label at top
                            render.translateTo(xWidth - render.measureText(dataLabel) / 2, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
                        } else { // scatter: put label at right
                            render.translateTo(xWidth + dataPointRadius + 2.0, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
                        }
                        render.renderLabel(dataLabel)
                    }
                    // line to next circle
                    if (hasLineBetweenPoints && j < xList.size() - 1) {
                        render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i]])
                        BigDecimal nextX = objectToNumber(xList[j + 1])
                        BigDecimal nextY = pointList[xList[j + 1]]
                        BigDecimal nextXWidth = diagramMarginLeft + (nextX - minX) / (maxX - minX) * totalWidth
                        BigDecimal nextYHeight = (nextY - startLabelY) / gapY * gapHeight
                        render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                        render.fillStyle(keyColor)
                        render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)
                        render.renderGroupEnd()
                    }
                }
            }
        } else { // discrete
            BigDecimal gapWidth = (render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight) / xLabelList.size()
            for (int i = 0; i < xLabelList.size(); i++) {
                BigDecimal xWidth = diagramMarginLeft + gapWidth * (i + 0.5)
                for (int j = 0; j < keys.size(); j++) {
                    List<BigDecimal> yList = dataPerKey[keys[j]].values() as List<BigDecimal>
                    if (!yList.isEmpty()) {
                        BigDecimal y = i < yList.size() ? yList[i] : 0.0
                        String yDataLabel = numberToString(y)
                        BigDecimal yHeight = (y - startLabelY) / gapY * gapHeight
                        Color keyColor = getKeyColor(j)

                        // data point
                        if (dataPointRadius > 0 && (!hasLineBetweenPoints || alwaysShowFullInfo || gapWidth >= MIN_GAP_WIDTH)) {
                            render.renderGroup(['element-type': ElementType.TOOLTIP,
                                                'key-label': keys[j],
                                                'key-color': KeyColor.colorToString(keyColor),
                                                'key-description': "${xLabelList[i]}: ${yDataLabel}",
                                                'diagram-action-url': diagramOption?.clickActionUrl ?: '',
                                                'data-x': xLabelList[i],
                                                'data-y': yDataLabel])
                            render.renderGroup(['element-type': ElementType.DATA,
                                                dataset: keys[j],
                                                style: 'pointer-events: bounding-box;'])
                            if (j < pointImageHref.size()) {
                                render.translateTo(xWidth - dataPointRadius, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius)
                                render.renderImage(pointImageHref[j], dataPointRadius * 2, dataPointRadius * 2)
                            } else {
                                render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                                render.fillStyle(keyColor)
                                render.renderCircle(dataPointRadius, IDiagramRender.DiagramStyle.fill)
                            }
                            if (dataPointClickableRadius > dataPointRadius) { // transport circle to enlarge clickable area
                                render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                                render.fillStyle(new Color(0, 0, 0, 0))
                                render.renderCircle(dataPointClickableRadius, IDiagramRender.DiagramStyle.fill)
                            }
                            render.renderGroupEnd()
                            render.renderGroupEnd()
                        }
                        // data label
                        if (diagramOption?.showDataCount && gapWidth >= MIN_GAP_WIDTH) {
                            if (y > startLabelY) {
                                if (hasLineBetweenPoints) { // line: put label at top
                                    render.translateTo(xWidth - render.measureText(yDataLabel) / 2, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - dataPointRadius - fontSize - 2.0)
                                } else { // scatter: put label at right
                                    render.translateTo(xWidth + dataPointRadius + 2.0, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight - fontSize / 2)
                                }
                                render.renderLabel(yDataLabel)
                            }
                        }
                        // line to next circle
                        if (hasLineBetweenPoints && i < xLabelList.size() - 1) {
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j]])
                            BigDecimal nextYHeight = ((i + 1 < yList.size() ? yList[i + 1] : 0.0) - startLabelY) / gapY * gapHeight
                            BigDecimal nextXWidth = diagramMarginLeft + gapWidth * (i + 1 + 0.5)
                            render.translateTo(xWidth, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - yHeight)
                            render.fillStyle(keyColor)
                            render.renderLine(nextXWidth - xWidth, yHeight - nextYHeight)
                            render.renderGroupEnd()
                        }
                    }
                }
            }
        }
    }

    void rootDraw(boolean alwaysShowFullInfo, Integer comboTotalCount, Integer comboCurrentCount) {
        super.draw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        if (!buildXLabelList()) {
            return
        }
        rootDraw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
        this.isXLabelInsideGap = !(xLabelList.every { it instanceof Number } || xLabelList.every { it instanceof Date })
        drawLegend(pointImageHref)
        drawHorizontalBackground()
        buildClipSectionStart()
        drawVerticalBackground()
        buildTransformAreaStart('scatter')
        drawDataPoint(false)
        render.renderGroupEnd()
        render.renderGroupEnd()
    }
}