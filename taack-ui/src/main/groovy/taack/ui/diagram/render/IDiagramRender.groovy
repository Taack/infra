package taack.ui.diagram.render

import taack.ui.base.common.Style

interface IDiagramRender {

    void translateTo(BigDecimal Double, BigDecimal y)

    void fillStyle(Style style)

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

    void renderVerticalLine(LineStyle style)

    void renderHorizontalLine(LineStyle style)

    void renderLine(BigDecimal toX, BigDecimal toY, LineStyle style)

    void renderHorizontalStrip(BigDecimal height, RectStyle style)

    void renderVerticalStrip(BigDecimal width, RectStyle style)

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

    void renderLabel(String label, LabelStyle style)

    enum TextJustify {
        LEFT,
        CENTER,
        RIGHT
    }

    enum RectStyle {
        DARK_BLUE,
        DARK_RED,
    }

    void renderRect(String label, BigDecimal width, BigDecimal height, RectStyle style)

    void renderRect(BigDecimal width, BigDecimal height, RectStyle style)

    void renderPoly(RectStyle style, BigDecimal... coords)

    void renderArrow(LineStyle style, BigDecimal... coords)

    BigDecimal pxInPxTransformed(BigDecimal pxY)

    void renderTriangle(BigDecimal length, boolean isDown)

}