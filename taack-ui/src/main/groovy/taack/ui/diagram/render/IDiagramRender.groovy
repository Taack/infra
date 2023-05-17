package taack.ui.diagram.render

import taack.ui.base.common.Style

import java.awt.Color

interface IDiagramRender {

    void translateTo(BigDecimal Double, BigDecimal y)

    void fillStyle(Color color)

    void strokeStyle(Style style)

    void lineWidth(BigDecimal width)

    enum LineStyle {
        DASHED,
        DASHED_EM,
        DASHED_BOLD,
        DASHED_EM_BOLD,
        PLAIN,
        PLAIN_BOLD,
        PLAIN_EM,
        PLAIN_EM_BOLD
    }

    void renderVerticalLine()

    void renderHorizontalLine()

    void renderLine(BigDecimal toX, BigDecimal toY)

    void renderHorizontalStrip(BigDecimal height)

    void renderVerticalStrip(BigDecimal width)

    enum LabelStyle {
        BLUE("#71cbff"),
        RED("#c01a11"),
        YELLOW("#c0c00f"),
        ORANGE("#c0740b"),
        PINK("lightsalmon")


        LabelStyle(String color) {
            this.color = color
        }
        String color

    }

    void renderLabel(String label)

    void renderSmallLabel(String label)

    enum TextJustify {
        LEFT,
        CENTER,
        RIGHT
    }

    enum RectStyle {
        DARK_BLUE,
        DARK_RED,
    }

    void renderRect(BigDecimal width, BigDecimal height)

    void renderPoly(BigDecimal... coords)

    void renderArrow(BigDecimal... coords)

    void renderTriangle(BigDecimal length, boolean isDown)

}