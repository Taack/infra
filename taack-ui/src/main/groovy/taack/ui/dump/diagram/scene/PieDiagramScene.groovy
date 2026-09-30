package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class PieDiagramScene extends DiagramScene {
    final private BigDecimal SLICE_DISTANCE_FROM_CENTER = 0.3 // the percentage to define how far the slice should be: 30% of the radius
    private BigDecimal OUTSIDE_LABEL_MARGIN = 2.0

    final private Map<String, BigDecimal> pieDataPerKey
    final private BigDecimal slicePositionRate

    PieDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption, boolean hasSlice) {
        super(render, diagramOption)
        this.slicePositionRate = hasSlice ? SLICE_DISTANCE_FROM_CENTER : 0.0

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            OUTSIDE_LABEL_MARGIN *= rate
        }

        Map<String, BigDecimal> pieDataPerKey = [:]
        Set<String> keys = dataPerKey.keySet()
        for (int i = 0; i < keys.size(); i++) {
            String key = keys[i]
            Collection<BigDecimal> pieDataList = dataPerKey[key].values()
            if (!pieDataList.isEmpty() && pieDataList.first() != null) {
                pieDataPerKey.put(key, pieDataList.first())
            } else {
                pieDataPerKey.put(key, BigDecimal.ZERO)
            }
        }
        this.pieDataPerKey = pieDataPerKey
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        super.draw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)

        drawTitle()

        BigDecimal radius = Math.min(((render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight) / 2 / 2).toDouble(), ((render.getDiagramHeight() - diagramMarginTop) / (2 + slicePositionRate)).toDouble())
        BigDecimal centerX = render.getDiagramWidth() / 2
        BigDecimal centerY = diagramMarginTop + radius * (1 + slicePositionRate)
        if (!pieDataPerKey.findAll { it.value > BigDecimal.ZERO }.isEmpty()) {
            BigDecimal total = pieDataPerKey.values().sum() as BigDecimal

            // sector
            BigDecimal angle1 = 0.0
            pieDataPerKey.eachWithIndex { Map.Entry<String, BigDecimal> it, int i ->
                if (it.value > BigDecimal.ZERO) {
                    Color keyColor = getKeyColor(i)
                    render.renderGroup(['element-type': ElementType.TOOLTIP,
//                                        'key-label': it.key,
//                                        'key-color': KeyColor.colorToString(keyColor),
//                                        'key-description': "${it.value}: ${(it.value / total * 100).round(2)}%",
                                        'diagram-action-url': diagramOption?.clickActionUrl ?: '',
                                        'data-x': it.key,
                                        'data-y': it.value])
                    render.renderGroup(['element-type': ElementType.DATA, dataset: it.key])
                    BigDecimal value = it.value
                    BigDecimal percent = value / total
                    BigDecimal angle2 = angle1 + 360.0 * percent
                    if (i == 0 && pieDataPerKey.size() > 1) {
                        Double sliceAngle = Math.toRadians((angle2 / 2).toDouble())
                        BigDecimal sliceCenterX = centerX + radius * Math.sin(sliceAngle) * slicePositionRate
                        BigDecimal sliceCenterY = centerY - radius * Math.cos(sliceAngle) * slicePositionRate
                        render.translateTo(sliceCenterX, sliceCenterY)
                    } else {
                        render.translateTo(centerX, centerY)
                    }
                    render.fillStyle(keyColor)
                    render.renderSector(radius, angle1, angle2, IDiagramRender.DiagramStyle.fill)
                    render.renderGroupEnd()
                    render.renderGroupEnd()

                    angle1 = angle2
                } else {
                    angle1 += 0.0
                }
            }

            // label
            angle1 = 0.0
            BigDecimal lastOutsideLabelX = -Long.MAX_VALUE
            BigDecimal lastOutsideLabelY = -Long.MAX_VALUE
            boolean drawByClockwise = true
            Set<String> keys = pieDataPerKey.findAll { it.value > BigDecimal.ZERO }.keySet()
            while (!keys.isEmpty()) {
                String key = drawByClockwise ? keys.first() : keys.last()
                BigDecimal value = pieDataPerKey[key]
                BigDecimal percent = value / total
                String valueLabel = "${numberToString(value)} (${(percent * 100).round(2)}%)"
                BigDecimal keyLabelLength = render.measureText(key)
                BigDecimal valueLabelLength = render.measureText(valueLabel)

                if (percent.toInteger() == 1) { // only one sector: draw label at center point
                    render.translateTo(centerX - keyLabelLength / 2, centerY - OUTSIDE_LABEL_MARGIN / 2 - fontSize)
                    render.renderLabel(key)
                    render.translateTo(centerX - valueLabelLength / 2, centerY + OUTSIDE_LABEL_MARGIN / 2)
                    render.renderLabel(valueLabel)
                } else { // draw label at the 3/4 of radius of the sector
                    // get the label position
                    Double startAngle = Math.toRadians(angle1.toDouble())
                    BigDecimal angle2 = angle1 + (drawByClockwise ? 360.0 * percent : -360.0 * percent)
                    Double endAngle = Math.toRadians(angle2.toDouble())
                    Double labelAngle = ((startAngle + endAngle) / 2) as Double
                    if (drawByClockwise && labelAngle > Math.PI) {
                        drawByClockwise = false
                        angle1 = 360.0
                        lastOutsideLabelX = Long.MAX_VALUE
                        lastOutsideLabelY = -Long.MAX_VALUE
                        continue
                    }
                    BigDecimal labelX = centerX + radius * Math.cos(labelAngle - Math.PI / 2) * (3 / 4 + (angle1 == 0.0 ? slicePositionRate : 0))
                    BigDecimal labelY = centerY + radius * Math.sin(labelAngle - Math.PI / 2) * (3 / 4 + (angle1 == 0.0 ? slicePositionRate : 0))
                    // get width (horizontal) and height (vertical) of the sector, calculated from the point of label position
                    BigDecimal startX = centerX + (centerY - labelY) * Math.tan(startAngle)
                    BigDecimal endX = centerX + (centerY - labelY) * Math.tan(endAngle)
                    BigDecimal startY = centerY - (labelX - centerX) * Math.tan(Math.PI / 2 - startAngle)
                    BigDecimal endY = centerY - (labelX - centerX) * Math.tan(Math.PI / 2 - endAngle)
                    // judge if the label could be completely included in the sector
                    BigDecimal maxLabelLength = Math.max(keyLabelLength.toDouble(), valueLabelLength.toDouble()).toBigDecimal()
                    if ((slicePositionRate > 0.0 && angle1 == 0.0)
                            || (Math.abs((endAngle - startAngle).toDouble()) < Math.PI
                            && (Math.abs((labelX - startX).toDouble()) < maxLabelLength / 2
                            || Math.abs((endX - labelX).toDouble()) < maxLabelLength / 2
                            || Math.abs((endY - startY).toDouble()) < fontSize * 2))) { // draw label outside
                        // but only when no slice mode or target label is of the slice
                        if (slicePositionRate == 0 || angle1 == 0.0) {
                            BigDecimal pointX = labelX + (radius / 4 + OUTSIDE_LABEL_MARGIN) * Math.cos(labelAngle - Math.PI / 2)
                            BigDecimal pointY = labelY + (radius / 4 + OUTSIDE_LABEL_MARGIN) * Math.sin(labelAngle - Math.PI / 2)
                            render.translateTo(labelX, labelY)
                            render.fillStyle(Color.BLACK)
                            render.renderLine(pointX - labelX, pointY - labelY)

                            BigDecimal horizontalLabelMargin = OUTSIDE_LABEL_MARGIN * 5
                            BigDecimal outsideLineLength = OUTSIDE_LABEL_MARGIN + keyLabelLength + render.measureText(': ') + valueLabelLength + OUTSIDE_LABEL_MARGIN
                            if (drawByClockwise) {
                                if (pointX > lastOutsideLabelX + horizontalLabelMargin || pointY - OUTSIDE_LABEL_MARGIN - fontSize - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(outsideLineLength, 0.0)
                                    BigDecimal x = Math.min(pointX.toDouble(), (render.getDiagramWidth() - outsideLineLength + OUTSIDE_LABEL_MARGIN).toDouble()).toBigDecimal() + OUTSIDE_LABEL_MARGIN
                                    render.translateTo(x, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(key + ': ' + valueLabel)
                                    lastOutsideLabelX = pointX + outsideLineLength
                                    lastOutsideLabelY = pointY
                                } else { // prolong line
                                    BigDecimal labelDrawingEndX = lastOutsideLabelX + horizontalLabelMargin + outsideLineLength
                                    if (labelDrawingEndX <= render.getDiagramWidth()) { // prolong line at horizontal direction
                                        render.translateTo(pointX, pointY)
                                        render.renderLine(labelDrawingEndX - pointX, 0.0)
                                        render.translateTo(labelDrawingEndX - outsideLineLength + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                        render.renderLabel(key + ': ' + valueLabel)
                                        lastOutsideLabelX = labelDrawingEndX
                                        lastOutsideLabelY = pointY
                                    } else { // prolong line at vertical direction
                                        BigDecimal point2X = centerX + radius * 5 / 4
                                        BigDecimal point2Y
                                        if (point2X + outsideLineLength - OUTSIDE_LABEL_MARGIN <= render.getDiagramWidth()) { // enough space to put all text in one line
                                            point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                            render.translateTo(pointX, pointY)
                                            render.renderLine(point2X - pointX, point2Y - pointY)
                                            render.translateTo(point2X, point2Y)
                                            render.renderLine(outsideLineLength, 0.0)
                                            render.translateTo(point2X + OUTSIDE_LABEL_MARGIN, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                            render.renderLabel(key + ': ' + valueLabel)
                                        } else { // text is overflowing horizontally, so show text by 2 lines
                                            point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                            render.translateTo(pointX, pointY)
                                            render.renderLine(point2X - pointX, point2Y - pointY)
                                            render.translateTo(point2X, point2Y)
                                            outsideLineLength = OUTSIDE_LABEL_MARGIN + maxLabelLength + OUTSIDE_LABEL_MARGIN
                                            render.renderLine(outsideLineLength, 0.0)
                                            BigDecimal x = Math.min((point2X + OUTSIDE_LABEL_MARGIN).toDouble(), (render.getDiagramWidth() - keyLabelLength).toDouble()).toBigDecimal()
                                            render.translateTo(x, lastOutsideLabelY + OUTSIDE_LABEL_MARGIN)
                                            render.renderLabel(key)
                                            x = Math.min((point2X + OUTSIDE_LABEL_MARGIN).toDouble(), (render.getDiagramWidth() - valueLabelLength).toDouble()).toBigDecimal()
                                            render.translateTo(x, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                            render.renderLabel(valueLabel)
                                        }
                                        lastOutsideLabelX = Long.MAX_VALUE
                                        lastOutsideLabelY = point2Y
                                    }
                                }
                            } else {
                                if (pointX < lastOutsideLabelX - horizontalLabelMargin || pointY - OUTSIDE_LABEL_MARGIN - fontSize - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(-outsideLineLength, 0.0)
                                    BigDecimal x = Math.max((pointX - outsideLineLength + OUTSIDE_LABEL_MARGIN).toDouble(), (0.0).toDouble()).toBigDecimal()
                                    render.translateTo(x, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(key + ': ' + valueLabel)
                                    lastOutsideLabelX = pointX - outsideLineLength
                                    lastOutsideLabelY = pointY
                                } else { // prolong line
                                    BigDecimal labelDrawingStartX = lastOutsideLabelX - horizontalLabelMargin - outsideLineLength
                                    if (labelDrawingStartX >= 0.0) { // prolong line at horizontal direction
                                        render.translateTo(pointX, pointY)
                                        render.renderLine(labelDrawingStartX - pointX, 0.0)
                                        render.translateTo(labelDrawingStartX + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                        render.renderLabel(key + ': ' + valueLabel)
                                        lastOutsideLabelX = labelDrawingStartX
                                        lastOutsideLabelY = pointY
                                    } else { // prolong line at vertical direction
                                        BigDecimal point2X = centerX - radius * 5 / 4
                                        BigDecimal point2Y
                                        if (point2X - outsideLineLength + OUTSIDE_LABEL_MARGIN >= 0) { // enough space to put all text in one line
                                            point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                            render.translateTo(pointX, pointY)
                                            render.renderLine(point2X - pointX, point2Y - pointY)
                                            render.translateTo(point2X, point2Y)
                                            render.renderLine(-outsideLineLength, 0.0)
                                            render.translateTo(point2X - outsideLineLength + OUTSIDE_LABEL_MARGIN, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                            render.renderLabel(key + ': ' + valueLabel)
                                        } else { // text is overflowing horizontally, so show text by 2 lines
                                            point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                            render.translateTo(pointX, pointY)
                                            render.renderLine(point2X - pointX, point2Y - pointY)
                                            render.translateTo(point2X, point2Y)
                                            outsideLineLength = OUTSIDE_LABEL_MARGIN + maxLabelLength + OUTSIDE_LABEL_MARGIN
                                            render.renderLine(-outsideLineLength, 0.0)
                                            BigDecimal x = Math.max((point2X - OUTSIDE_LABEL_MARGIN - keyLabelLength).toDouble(), (0.0).toDouble()).toBigDecimal()
                                            render.translateTo(x, lastOutsideLabelY + OUTSIDE_LABEL_MARGIN)
                                            render.renderLabel(key)
                                            x = Math.max((point2X - OUTSIDE_LABEL_MARGIN - valueLabelLength).toDouble(), (0.0).toDouble()).toBigDecimal()
                                            render.translateTo(x, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                            render.renderLabel(valueLabel)
                                        }
                                        lastOutsideLabelX = -Long.MAX_VALUE
                                        lastOutsideLabelY = point2Y
                                    }
                                }
                            }
                        }
                    } else { // draw label inside
                        render.translateTo(labelX - keyLabelLength / 2, labelY - OUTSIDE_LABEL_MARGIN / 2 - fontSize)
                        render.renderLabel(key)
                        render.translateTo(labelX - valueLabelLength / 2, labelY + OUTSIDE_LABEL_MARGIN / 2)
                        render.renderLabel(valueLabel)
                    }
                    angle1 = angle2
                }
                keys.remove(key)
            }
        } else {
            render.translateTo(centerX, centerY)
            render.fillStyle(KeyColor.GREY.color)
            render.renderCircle(radius, IDiagramRender.DiagramStyle.fill)

            String label = 'No data'
            render.translateTo(centerX - render.measureText(label) / 2, centerY - fontSize / 2)
            render.renderLabel(label)
        }
    }
}