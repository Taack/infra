package taack.ui.diagram.render

import taack.ui.base.common.Style

interface IDiagramRender {
    void rotate(BigDecimal degrees)

    void translateTo(BigDecimal Double, BigDecimal y)

    void fillStyle(Style style)

    void strokeStyle(Style style)

    void lineWidth(BigDecimal width)

    void swapOrientation()

    void changeZoom()

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

    enum StripStyle {
        LIGHT_GREY,
        DARK_GREY
    }

    void renderHorizontalStrip(BigDecimal height, StripStyle style)

    void renderVerticalStrip(BigDecimal width, StripStyle style)

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

    void renderSmallLabel(String label)

enum TextJustify {
LEFT,
CENTER,
RIGHT
}

void renderLabelMonospaced (String label, BigDecimal height, BigDecimal boxWith, BigDecimal boxHeight, TextJustify justify)

enum RectStyle {
DARK_BLUE,
DARK_RED,
}

void renderRect(String label, BigDecimal width, BigDecimal height, RectStyle style)

void renderRect (BigDecimal width, BigDecimal height, RectStyle style)

void renderPoly (RectStyle style, BigDecimal... coords)

void renderArrow (LineStyle style, BigDecimal... coords)

BigDecimal pxInPxTransformed (BigDecimal pxY)

void renderTriangle (BigDecimal length, boolean isDown)

//fun getVerticalScrollBarPercentage ( ): Double
//
//fun renderVerticalScrollBar ( isClicked : Boolean )
//
//fun getCanvasWidth ( ): Int
//
//fun getCanvasHeight ( ): Int
//
//fun clearRect ( width: Double, height: Double, isWidthZoom : Boolean = false, isHeightZoom: Boolean = false )
//
//fun clearAll ( )
//
//fun measureText ( label: String ): Double
//
//fun isClicked ( ): Boolean
//
//fun isMoving ( ): Boolean
//
//fun getDisplayedDateNumbers ( widthPerDay: Double ): Pair < Int, Int >

}