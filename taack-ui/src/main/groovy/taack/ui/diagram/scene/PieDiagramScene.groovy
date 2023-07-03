package taack.ui.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.diagram.render.IDiagramRender

import java.awt.Color

@CompileStatic
class PieDiagramScene extends DiagramScene {

    final private BigDecimal OUTSIDE_LABEL_MARGIN = 2.0

    PieDiagramScene(IDiagramRender render, Map<String, List<BigDecimal>> yDataPerKey) {
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.yDataPerKey = yDataPerKey
        this.legendFullColor = true
    }

    void drawDataPie() {
        BigDecimal total = yDataPerKey.values().collect { it.size() ? it.first() : 0 }.sum() as BigDecimal
        BigDecimal radius = (Math.min(((width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / 2).toDouble(), (height - diagramMarginTop - 5.0).toDouble()) / 2).toBigDecimal()
        BigDecimal centerX = width / 2
        BigDecimal centerY = diagramMarginTop + radius

        // sector
        BigDecimal angle1 = 0.0
        yDataPerKey.eachWithIndex { Map.Entry<String, List<BigDecimal>> it, int i ->
            BigDecimal value = it.value.size() ? it.value.first() : 0.0
            BigDecimal percent = value / total
            BigDecimal angle2 = angle1 + 360.0 * percent
            render.translateTo(centerX, centerY)
            render.fillStyle(LegendColor.colorFrom(i))
            render.renderSector(radius, angle1, angle2, IDiagramRender.DiagramStyle.fill)

            angle1 = angle2
        }

        // label
        angle1 = 0.0
        BigDecimal lastOutsideLabelX = -1000.0
        BigDecimal lastOutsideLabelY = -1000.0
        boolean drawByClockwise = true
        Set<String> keys = yDataPerKey.keySet()
        while (!keys.isEmpty()) {
            String key = drawByClockwise ? keys.first() : keys.last()
            BigDecimal value = yDataPerKey[key].size() ? yDataPerKey[key].first() : 0.0
            if (value != 0.0) {
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
                BigDecimal labelX = centerX + radius * Math.cos(labelAngle - Math.PI / 2) * 3 / 4
                BigDecimal labelY = centerY + radius * Math.sin(labelAngle - Math.PI / 2) * 3 / 4
                // get width (horizontal) and height (vertical) of the sector, calculated from the point of label position
                BigDecimal startX = centerX + (centerY - labelY) * Math.tan(startAngle)
                BigDecimal endX = centerX + (centerY - labelY) * Math.tan(endAngle)
                BigDecimal startY = centerY - (labelX - centerX) * Math.tan(Math.PI / 2 - startAngle)
                BigDecimal endY = centerY - (labelX - centerX) * Math.tan(Math.PI / 2 - endAngle)
                // judge if the label could be completely included in the sector
                if (((Math.abs((labelX - startX).toDouble()) >= maxLabelLength / 2)
                        && (Math.abs((endX - labelX).toDouble()) >= maxLabelLength / 2)
                        && (Math.abs((endY - startY).toDouble()) >= FONT_SIZE * 2))
                        || (Math.abs((endAngle - startAngle).toDouble()) >= Math.PI)) { // draw label inside
                    render.translateTo(labelX - valueLabelLength / 2, labelY - FONT_SIZE)
                    render.renderLabel(valueLabel)
                    render.translateTo(labelX - percentLabelLength / 2, labelY)
                    render.renderLabel(percentLabel)
                } else { // draw label outside
                    BigDecimal pointX = centerX + (radius + OUTSIDE_LABEL_MARGIN) * Math.cos(labelAngle - Math.PI / 2)
                    BigDecimal pointY = centerY + (radius + OUTSIDE_LABEL_MARGIN) * Math.sin(labelAngle - Math.PI / 2)
                    render.translateTo(labelX, labelY)
                    render.fillStyle(Color.BLACK)
                    render.renderLine(pointX - labelX, pointY - labelY)

                    BigDecimal outsideLineLength = OUTSIDE_LABEL_MARGIN + valueLabelLength + percentLabelLength + OUTSIDE_LABEL_MARGIN
                    if (drawByClockwise) {
                        if (pointX > lastOutsideLabelX + OUTSIDE_LABEL_MARGIN || pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                            render.translateTo(pointX, pointY)
                            render.renderLine(outsideLineLength, 0.0)
                            render.translateTo(pointX + OUTSIDE_LABEL_MARGIN, pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                            render.renderLabel(valueLabel + " " + percentLabel)
                            lastOutsideLabelX = pointX + outsideLineLength
                            lastOutsideLabelY = pointY
                        } else { // prolong line
                            if (lastOutsideLabelX + OUTSIDE_LABEL_MARGIN + outsideLineLength <= width) { // prolong line at horizontal direction
                                render.translateTo(pointX, pointY)
                                render.renderLine(lastOutsideLabelX - pointX + OUTSIDE_LABEL_MARGIN + outsideLineLength, 0.0)
                                render.translateTo(lastOutsideLabelX + OUTSIDE_LABEL_MARGIN * 2, pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = lastOutsideLabelX + OUTSIDE_LABEL_MARGIN + outsideLineLength
                                lastOutsideLabelY = pointY
                            } else { // prolong line at vertical direction
                                BigDecimal point2X = centerX + radius * 5 / 4
                                BigDecimal point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + FONT_SIZE + OUTSIDE_LABEL_MARGIN
                                render.translateTo(pointX, pointY)
                                render.renderLine(point2X - pointX, point2Y - pointY)
                                render.translateTo(point2X, point2Y)
                                render.renderLine(outsideLineLength, 0.0)
                                render.translateTo(point2X + OUTSIDE_LABEL_MARGIN, point2Y - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = width
                                lastOutsideLabelY = point2Y
                            }
                        }
                    } else {
                        if (pointX < lastOutsideLabelX - OUTSIDE_LABEL_MARGIN || pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE - OUTSIDE_LABEL_MARGIN > lastOutsideLabelY) { // normal
                            render.translateTo(pointX, pointY)
                            render.renderLine(-outsideLineLength, 0.0)
                            render.translateTo(pointX - outsideLineLength, pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                            render.renderLabel(valueLabel + " " + percentLabel)
                            lastOutsideLabelX = pointX - outsideLineLength
                            lastOutsideLabelY = pointY
                        } else { // prolong line
                            BigDecimal labelDrawingStartX = lastOutsideLabelX - OUTSIDE_LABEL_MARGIN - outsideLineLength
                            if (labelDrawingStartX >= 0.0) { // prolong line at horizontal direction
                                render.translateTo(pointX, pointY)
                                render.renderLine(-(pointX - labelDrawingStartX), 0.0)
                                render.translateTo(labelDrawingStartX, pointY - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = labelDrawingStartX
                                lastOutsideLabelY = pointY
                            } else { // prolong line at vertical direction
                                BigDecimal point2X = centerX - radius * 5 / 4
                                BigDecimal point2Y = lastOutsideLabelY + OUTSIDE_LABEL_MARGIN + FONT_SIZE + OUTSIDE_LABEL_MARGIN
                                render.translateTo(pointX, pointY)
                                render.renderLine(point2X - pointX, point2Y - pointY)
                                render.translateTo(point2X, point2Y)
                                render.renderLine(-outsideLineLength, 0.0)
                                render.translateTo(point2X - outsideLineLength, point2Y - OUTSIDE_LABEL_MARGIN - FONT_SIZE)
                                render.renderLabel(valueLabel + " " + percentLabel)
                                lastOutsideLabelX = 0.0
                                lastOutsideLabelY = point2Y
                            }
                        }
                    }
                }
                angle1 = angle2
            }
            keys.remove(key)
        }
    }

    void draw() {
        if (yDataPerKey.keySet().isEmpty()) {
            return
        }
        drawLegend()
        drawDataPie()
    }
}