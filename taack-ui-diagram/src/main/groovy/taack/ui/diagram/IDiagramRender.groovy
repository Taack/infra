package diagram

import java.awt.Color

interface IDiagramRender {
    enum DiagramStyle {
        fill,
        stroke,
    }

    void translateTo(BigDecimal x, BigDecimal y)

    void fillStyle(Color color)

    void lineWidth(BigDecimal width)

    void renderVerticalLine()

    void renderHorizontalLine()

    void renderLine(BigDecimal toX, BigDecimal toY)

    void renderHorizontalStrip(BigDecimal height)

    void renderVerticalStrip(BigDecimal width)

    void renderLabel(String label)

    void renderHiddenLabel(String label)

    void renderSmallLabel(String label)

    void renderRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY)

    void renderHiddenRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY)

    void renderRect(BigDecimal width, BigDecimal height, DiagramStyle diagramStyle)

    void renderCircle(BigDecimal radius, DiagramStyle diagramStyle)

    void renderPoly(List<BigDecimal> coords, DiagramStyle diagramStyle)

    void renderArrow(BigDecimal... coords)

    void renderTriangle(BigDecimal length, boolean isDown)

    void renderSector(BigDecimal r, BigDecimal angle1, BigDecimal angle2, DiagramStyle diagramStyle)

    void renderImage(String filepath, BigDecimal width, BigDecimal height)

    void renderGroup(Map attributes)

    void renderGroupEnd()

    void renderClipSection(String id, List<BigDecimal> coords)

    BigDecimal getFontSize()

    BigDecimal getDiagramWidth()

    BigDecimal getDiagramHeight()

    BigDecimal measureText(String text)
}