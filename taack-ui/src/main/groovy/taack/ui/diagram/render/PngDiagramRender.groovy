package taack.ui.diagram.render

import groovy.transform.CompileStatic
import taack.ui.base.common.Style

import javax.imageio.ImageIO
import java.awt.*
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

@CompileStatic
class PngDiagramRender implements IDiagramRender {
    final BufferedImage bi
    final Graphics2D ig2
    private final BigDecimal pngWidth
    private final BigDecimal pngHeight
    Font currentFont = new Font("SansSerif", Font.PLAIN, 13)
    private final FontMetrics fm

    private BigDecimal trX = 0.0
    private BigDecimal trY = 0.0
    private Color fillStyle = Color.BLACK

    PngDiagramRender(BigDecimal width, BigDecimal height) {
        pngWidth = width
        pngHeight = height
        bi = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB)
        ig2 = bi.createGraphics()
        ig2.setPaint(fillStyle)
        ig2.setFont(currentFont)
        fm = ig2.getFontMetrics(currentFont)
    }

    @Override
    void translateTo(BigDecimal x, BigDecimal y) {
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/Graphics2D.html
        trX = x
        trY = y

    }

    @Override
    void fillStyle(Color color) {
        fillStyle = color
    }

    @Override
    void strokeStyle(Style style) {

    }

    @Override
    void lineWidth(BigDecimal width) {

    }

    @Override
    void renderVerticalLine() {
        renderLine(0.0, pngHeight - trY)
    }

    @Override
    void renderHorizontalLine() {
        renderLine(pngWidth - trX, 0.0)
    }

    @Override
    void renderLine(BigDecimal toX, BigDecimal toY) {
        ig2.setPaint(fillStyle)
        ig2.draw(new Line2D.Double(trX.toDouble(), trY.toDouble(), (toX + trX).toDouble(), (toY + trY).toDouble()))
    }

    @Override
    void renderHorizontalStrip(BigDecimal height) {
        renderRect(pngWidth - trX, height)
    }

    @Override
    void renderVerticalStrip(BigDecimal width) {
        renderRect(width, pngHeight - trY)
    }

    @Override
    void renderLabel(String label) {
        ig2.setPaint(Color.BLACK)
        ig2.drawString(label, trX.toInteger(), (trY + 13.0 - 2.0).toInteger())
        ig2.setPaint(fillStyle)
    }

    @Override
    void renderSmallLabel(String label) {
        ig2.setPaint(Color.BLACK)
        ig2.setFont(new Font("SansSerif", Font.PLAIN, 10))
        ig2.drawString(label, trX.toInteger(), (trY + 13.0 - 2.0).toInteger())
        ig2.setFont(new Font("SansSerif", Font.PLAIN, 13))
        ig2.setPaint(fillStyle)
    }

    @Override
    void renderRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY) {
        // todo
    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height, RectStyle rectStyle = RectStyle.fill) {
        ig2.setPaint(fillStyle) // todo: stroke
        ig2.fill(new Rectangle2D.Double(trX.toDouble(), trY.toDouble(), width.toDouble(), height.toDouble()))
    }

    @Override
    void renderPoly(BigDecimal... coords) {
        def p = new Polygon()
        def it = coords.iterator()
        ig2.setPaint(fillStyle)
        while (it.hasNext()) {
            p.addPoint((it.next() + trX).toInteger(), (it.next() + trY).toInteger())
        }
        ig2.fill(p)
    }

    @Override
    void renderArrow(BigDecimal... coords) { // ARROW_LENGTH = 8.0
        def it = coords.iterator()
        ig2.setPaint(Color.BLACK)
        ArrayList<Integer> xPoints = new ArrayList(trX.toInteger())
        ArrayList<Integer> yPoints = new ArrayList(trY.toInteger())
        BigDecimal x1 = 0.0
        BigDecimal y1 = 0.0
        BigDecimal x2 = 0.0
        BigDecimal y2 = 0.0

        while (it.hasNext()) {
            x1 = x2
            y1 = y2
            x2 = it.next()
            y2 = it.next()
            xPoints.add((trX + x2).toInteger())
            yPoints.add((trY + y2).toInteger())
        }
        Double angle = Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        xPoints.add((trX + x2 - 8.0 * Math.cos(angle - Math.PI / 6)).toInteger())
        yPoints.add((trY + (y2 - 8.0 * Math.sin(angle - Math.PI / 6))).toInteger())
        xPoints.add((trX + x2).toInteger())
        yPoints.add((trY + y2).toInteger())
        xPoints.add((trX + x2 - 8.0 * Math.cos(angle + Math.PI / 6)).toInteger())
        yPoints.add((trY + (y2 - 8.0 * Math.sin(angle + Math.PI / 6))).toInteger())
        ig2.drawPolyline(xPoints as int[], yPoints as int[], xPoints.size())
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {
        def tmp = fillStyle
        fillStyle = Color.BLACK
        if (isDown) {
            renderPoly(
                    0.0, 0.0,
                    length, 0.0,
                    length / 2.0, length
            )
        }
        else {
            renderPoly(
                    0.0, 0.0,
                    length, length / 2.0,
                    0.0, length
            )
        }
        fillStyle = tmp
    }

    @Override
    BigDecimal getDiagramWidth() {
        return pngWidth
    }

    @Override
    BigDecimal getDiagramHeight() {
        return pngHeight
    }

    @Override
    BigDecimal measureText(String text) {
        return fm.stringWidth(text)
    }

    void writeImage(OutputStream os) {
        ImageIO.write(bi, 'PNG', os)
    }
}
