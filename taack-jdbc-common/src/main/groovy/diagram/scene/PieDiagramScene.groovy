package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class PieDiagramScene extends DiagramScene {
    final private BigDecimal SLICE_DISTANCE_FROM_CENTER = 0.3 // define how far the slice should be: 0.3 * radius
    final private BigDecimal OUTSIDE_LABEL_MARGIN = 2.0

    final private Map<String, BigDecimal> pieDataPerKey
    final private BigDecimal slicePositionRate

    PieDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, boolean hasSlice) {
        this.fontSize = render.getFontSize()
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.slicePositionRate = hasSlice ? SLICE_DISTANCE_FROM_CENTER : 0.0

        Map<String, BigDecimal> pieDataPerKey = [:]
        Set<String> keys = dataPerKey.keySet()
        for (int i = 0; i < keys.size(); i++) {
            String key = keys[i]
            Collection<BigDecimal> pieDataList = dataPerKey[key].values()
            if (!pieDataList.isEmpty() && pieDataList.first() != BigDecimal.ZERO) {
                pieDataPerKey.put(key, pieDataList.first())
            }
        }
        this.pieDataPerKey = pieDataPerKey
    }

    void draw() {
        BigDecimal radius = Math.min(((width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / 2 / 2).toDouble(), ((height - DIAGRAM_MARGIN_TOP - 5.0) / (2 + slicePositionRate)).toDouble())
        BigDecimal centerX = width / 2
        BigDecimal centerY = DIAGRAM_MARGIN_TOP + radius * (1 + slicePositionRate)
        if (!pieDataPerKey.isEmpty()) {
            BigDecimal total = pieDataPerKey.values().sum() as BigDecimal

            // sector
            BigDecimal angle1 = 0.0
            pieDataPerKey.eachWithIndex { Map.Entry<String, BigDecimal> it, int i ->
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
                render.fillStyle(KeyColor.colorFrom(i).color)
                render.renderSector(radius, angle1, angle2, IDiagramRender.DiagramStyle.fill)

                angle1 = angle2
            }

            // label
            angle1 = 0.0
            BigDecimal lastOutsideLabelX = -1000.0
            BigDecimal lastOutsideLabelY = -1000.0
            boolean drawByClockwise = true
            Set<String> keys = pieDataPerKey.keySet()
            while (!keys.isEmpty()) {
                String key = drawByClockwise ? keys.first() : keys.last()
                BigDecimal value = pieDataPerKey[key]
                String valueLabel = "${key}: ${value.toDouble() % 1 == 0 ? "${value.toInteger()}" : "$value"}"
                BigDecimal valueLabelLength = render.measureText(valueLabel)
                BigDecimal percent = value / total
                String percentLabel = "(${(percent * 100).round(2)}%)"
                BigDecimal percentLabelLength = render.measureText(percentLabel)
                BigDecimal maxLabelLength = Math.max(valueLabelLength.toDouble(), percentLabelLength.toDouble()).toBigDecimal()

                // get the label position (It is at the 3/4 of radius of the sector)
                Double startAngle = Math.toRadians(angle1.toDouble())
                BigDecimal angle2 = angle1 + (drawByClockwise ? 360.0 * percent : -360.0 * percent)
                Double endAngle = Math.toRadians(angle2.toDouble())
                Double labelAngle = ((startAngle + endAngle) / 2) as Double
                if (drawByClockwise && labelAngle > Math.PI) {
                    drawByClockwise = false
                    angle1 = 360.0
                    lastOutsideLabelX = 100000.0
                    lastOutsideLabelY = -1000.0
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

                        BigDecimal outsideLineLength = OUTSIDE_LABEL_MARGIN + valueLabelLength + render.measureText(" ") + percentLabelLength + OUTSIDE_LABEL_MARGIN
                        if (drawByClockwise) {
                            if (pointX > lastOutsideLabelX + OUTSIDE_LABEL_MARGIN || pointY - OUTSIDE_LABEL_MARGIN - fontSize - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                                render.translateTo(pointX, pointY)
                                render.renderLine(outsideLineLength, 0.0)
                                render.translateTo(pointX + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = pointX + outsideLineLength
                                lastOutsideLabelY = pointY
                            } else { // prolong line
                                BigDecimal marginBetweenLabels = OUTSIDE_LABEL_MARGIN * 5
                                if (lastOutsideLabelX + marginBetweenLabels + outsideLineLength <= width) { // prolong line at horizontal direction
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(lastOutsideLabelX - pointX + marginBetweenLabels + outsideLineLength, 0.0)
                                    render.translateTo(lastOutsideLabelX + marginBetweenLabels + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(valueLabel + " " + percentLabel)
                                    lastOutsideLabelX = lastOutsideLabelX + marginBetweenLabels + outsideLineLength
                                    lastOutsideLabelY = pointY
                                } else { // prolong line at vertical direction
                                    BigDecimal point2X = centerX + radius * 5 / 4
                                    BigDecimal point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(point2X - pointX, point2Y - pointY)
                                    render.translateTo(point2X, point2Y)
                                    render.renderLine(outsideLineLength, 0.0)
                                    render.translateTo(point2X + OUTSIDE_LABEL_MARGIN, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(valueLabel + " " + percentLabel)
                                    lastOutsideLabelX = width
                                    lastOutsideLabelY = point2Y
                                }
                            }
                        } else {
                            if (pointX < lastOutsideLabelX - OUTSIDE_LABEL_MARGIN || pointY - OUTSIDE_LABEL_MARGIN - fontSize - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                                render.translateTo(pointX, pointY)
                                render.renderLine(-outsideLineLength, 0.0)
                                render.translateTo(pointX - outsideLineLength + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = pointX - outsideLineLength
                                lastOutsideLabelY = pointY
                            } else { // prolong line
                                BigDecimal labelDrawingStartX = lastOutsideLabelX - OUTSIDE_LABEL_MARGIN * 5 - outsideLineLength
                                if (labelDrawingStartX >= 0.0) { // prolong line at horizontal direction
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(-(pointX - labelDrawingStartX), 0.0)
                                    render.translateTo(labelDrawingStartX + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(valueLabel + " " + percentLabel)
                                    lastOutsideLabelX = labelDrawingStartX
                                    lastOutsideLabelY = pointY
                                } else { // prolong line at vertical direction
                                    BigDecimal point2X = centerX - radius * 5 / 4
                                    BigDecimal point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + fontSize + OUTSIDE_LABEL_MARGIN
                                    render.translateTo(pointX, pointY)
                                    render.renderLine(point2X - pointX, point2Y - pointY)
                                    render.translateTo(point2X, point2Y)
                                    render.renderLine(-outsideLineLength, 0.0)
                                    render.translateTo(point2X - outsideLineLength + OUTSIDE_LABEL_MARGIN, point2Y - OUTSIDE_LABEL_MARGIN - fontSize)
                                    render.renderLabel(valueLabel + " " + percentLabel)
                                    lastOutsideLabelX = 0.0
                                    lastOutsideLabelY = point2Y
                                }
                            }
                        }
                    }
                } else { // draw label inside
                    render.translateTo(labelX - valueLabelLength / 2, labelY - fontSize)
                    render.renderLabel(valueLabel)
                    render.translateTo(labelX - percentLabelLength / 2, labelY)
                    render.renderLabel(percentLabel)
                }
                angle1 = angle2
                keys.remove(key)
            }
        } else {
            render.translateTo(centerX, centerY)
            render.fillStyle(KeyColor.GREY.color)
            render.renderCircle(radius, IDiagramRender.DiagramStyle.fill)

            String label = "No data"
            render.translateTo(centerX - render.measureText(label) / 2, centerY - fontSize / 2)
            render.renderLabel(label)
        }
    }
}