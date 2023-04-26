package taack.ui.diagram.render

import groovy.transform.CompileStatic
import taack.ui.base.common.Style

import javax.imageio.ImageIO
import java.awt.*
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
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
    BasicStroke stroke = new BasicStroke(10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.1F)

    CanvasDiagramRender(int width, int height) {
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        ig2 = bi.createGraphics()
        ig2.setPaint(Color.black)
        ig2.setFont(currentFont)
    }

    @Override
    void translateTo(BigDecimal x, BigDecimal y) {
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/Graphics2D.html
        trX = x
        trY = y

    }

    @Override
    void fillStyle(Style style) {

    }

    @Override
    void strokeStyle(Style style) {
        ig2.setPaint(Color.black)
    }

    @Override
    void lineWidth(BigDecimal width) {

    }

    @Override
    void renderVerticalLine(LineStyle style) {
        renderLine(0.0, boundY, style)
    }

    @Override
    void renderHorizontalLine(LineStyle style) {
        renderLine(boundX, 0.0, style)
    }

    @Override
    void renderLine(BigDecimal toX, BigDecimal toY, LineStyle style) {
        ig2.draw(stroke.createStrokedShape(new Line2D.Double((trX * pxPerMmX * scaleX).toDouble(), (trY * pxPerMmY * scaleY).toDouble(), toX.toDouble(), toY.toDouble())))
    }

    @Override
    void renderHorizontalStrip(BigDecimal height, RectStyle style) {
        renderRect(boundX, height, style)
    }

    @Override
    void renderVerticalStrip(BigDecimal width, RectStyle style) {
        renderRect(width, boundY, style)
    }

    @Override
    void renderLabel(String label, LabelStyle style = null) {
        var tTrY = trY

        if (trY <= fontHeightPx / pxPerMmY) {
            tTrY = fontHeightPx / pxPerMmY
        }

        ig2.drawString(label, (trX * pxPerMmX * scaleX).toInteger(), (tTrY * pxPerMmY * scaleY).toInteger())
    }

    @Override
    void renderRect(String label, BigDecimal width, BigDecimal height, RectStyle style) {
        renderLabel(label)
        renderRect(width, height, style)
    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height, RectStyle style) {
        if (style == null) {
            ig2.draw(stroke.createStrokedShape(new Rectangle2D.Double((trX * pxPerMmX * scaleX).toDouble(), (trY * pxPerMmY * scaleY).toDouble(), (width * pxPerMmX * scaleX).toDouble(), (height * pxPerMmY * scaleY).toDouble())))
        } else {
            ig2.setPaint(style == RectStyle.DARK_BLUE ? Color.blue : Color.red)
            ig2.draw(stroke.createStrokedShape(new Rectangle2D.Double((trX * pxPerMmX * scaleX).toDouble(), (trY * pxPerMmY * scaleY).toDouble(), (width * pxPerMmX * scaleX).toDouble(), (height * pxPerMmY * scaleY).toDouble())))
            ig2.setPaint(Color.black)
        }
    }

    @Override
    void renderPoly(RectStyle style, BigDecimal... coords) {
        def p = new Polygon()
        def it = coords.iterator()
        while (it.hasNext()) {
            p.addPoint(((it.next() + trX) * pxPerMmX * scaleX).toInteger(), ((it.next() + trY) * pxPerMmY * scaleY).toInteger())
        }
        ig2.draw(stroke.createStrokedShape(p))
    }

    @Override
    void renderArrow(LineStyle style, BigDecimal... coords) {
        int headLen = 1
        def p = new Polygon()
        def it = coords.iterator()
        var dx = 0.0
        var dy = 0.0
        var tdx = 0.0
        var tdy = 0.0
        var tmpTdx = 0.0
        var tmpTdy = 0.0
        p.addPoint((trX * pxPerMmX).toInteger(), (trY * pxPerMmY).toInteger())
        while (it.hasNext()) {
            tdx = it.next()
            tdy = it.next()
            p.addPoint((tdx * pxPerMmX * scaleX).toInteger(), (tdy * pxPerMmY * scaleY).toInteger())
            dx = tdx - tmpTdx
            dy = tdy - tmpTdy
            tmpTdx = tdx
            tmpTdy = tdy
        }
        def angle = Math.atan2(dy.toDouble(), dx.toDouble())
        p.addPoint(((tdx - headLen * Math.cos(angle - Math.PI / 6)) * pxPerMmX).toInteger(), ((tdy - headLen * Math.sin(angle - Math.PI / 6)) * pxPerMmY).toInteger())
        p.addPoint((tdx * pxPerMmX * scaleX).toInteger(), (tdy * pxPerMmY * scaleY).toInteger())
        p.addPoint(((tdx - headLen * Math.cos(angle + Math.PI / 6)) * pxPerMmX).toInteger(), ((tdy - headLen * Math.sin(angle + Math.PI / 6)) * pxPerMmY).toInteger())
        ig2.draw(stroke.createStrokedShape(p))
    }

    @Override
    BigDecimal pxInPxTransformed(BigDecimal pxY) {
        return pxY.toDouble() * 297 / 1160
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {
        if (isDown) {
            renderPoly(
                    null, 0.0,
                    length, 0.0,
                    length / 2.0, length * Math.sin(Math.PI / 3.0d) as BigDecimal
            )
        }
        else {
            renderPoly(
                    null, 0.0,
                    0.0, length,
                    length * Math.sin(Math.PI / 3.0d) as BigDecimal, length / 2.0
            )
        }
    }

    void writeImage(OutputStream os) {
        ImageIO.write(bi, 'PNG', os)
    }
}
