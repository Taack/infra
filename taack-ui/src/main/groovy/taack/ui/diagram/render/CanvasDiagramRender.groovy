package taack.ui.diagram.render

import groovy.transform.CompileStatic
import taack.ui.base.common.Style

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage


@CompileStatic
class CanvasDiagramRender implements IDiagramRender {
    final BufferedImage bi
    final Graphics2D ig2
    private final BigDecimal boundX
    private final BigDecimal boundY
    private BigDecimal trX = 0.0
    private BigDecimal trY = 0.0
    private Style fillStyle
    private Style strokeStyle
    private BigDecimal pxPerMmX
    private BigDecimal pxPerMmY
    private BigDecimal scaleX
    private BigDecimal scaleY
    private BigDecimal fontHeightPx = 10.0
    Font currentFont = new Font("SansSerif", Font.BOLD, 20)

    CanvasDiagramRender(int width, int height) {
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        ig2 = bi.createGraphics()
        ig2.setPaint(Color.black)
    }

    @Override
    void translateTo(BigDecimal Double, BigDecimal y) {
//        https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/Graphics2D.html
    }

    @Override
    void fillStyle(Style style) {

    }

    @Override
    void strokeStyle(Style style) {

    }

    @Override
    void lineWidth(BigDecimal width) {

    }

    @Override
    void renderVerticalLine(LineStyle style) {

    }

    @Override
    void renderHorizontalLine(LineStyle style) {

    }

    @Override
    void renderLine(BigDecimal toX, BigDecimal toY, LineStyle style) {

    }

    @Override
    void renderHorizontalStrip(BigDecimal height, RectStyle style) {

    }

    @Override
    void renderVerticalStrip(BigDecimal width, RectStyle style) {

    }

    @Override
    void renderLabel(String label, LabelStyle style) {

    }

    @Override
    void renderRect(String label, BigDecimal width, BigDecimal height, RectStyle style) {

    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height, RectStyle style) {

    }

    @Override
    void renderPoly(RectStyle style, BigDecimal... coords) {

    }

    @Override
    void renderArrow(LineStyle style, BigDecimal... coords) {

    }

    @Override
    BigDecimal pxInPxTransformed(BigDecimal pxY) {
        return null
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {

    }

    void writeImage(OutputStream os) {
        ImageIO.write(bi, 'PNG', os)
    }
}
