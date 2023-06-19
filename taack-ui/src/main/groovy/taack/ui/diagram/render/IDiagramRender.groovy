package taack.ui.diagram.render

import taack.ui.base.common.Style

import java.awt.Color

interface IDiagramRender {

    void translateTo(BigDecimal x, BigDecimal y)

    void fillStyle(Color color)

    void strokeStyle(Style style)

    void lineWidth(BigDecimal width)

    void renderVerticalLine()

    void renderHorizontalLine()

    void renderLine(BigDecimal toX, BigDecimal toY)

    void renderHorizontalStrip(BigDecimal height)

    void renderVerticalStrip(BigDecimal width)

    void renderLabel(String label)

    void renderSmallLabel(String label)

    void renderRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY)

    enum RectStyle {
        fill,
        stroke,
    }
    void renderRect(BigDecimal width, BigDecimal height, RectStyle rectStyle)

    enum CircleStyle {
        fill,
        stroke,
    }
    void renderCircle(BigDecimal radius, CircleStyle circleStyle)

    void renderPoly(BigDecimal... coords)

    void renderArrow(BigDecimal... coords)

    void renderTriangle(BigDecimal length, boolean isDown)

    BigDecimal getDiagramWidth()

    BigDecimal getDiagramHeight()

    BigDecimal measureText(String text)
}