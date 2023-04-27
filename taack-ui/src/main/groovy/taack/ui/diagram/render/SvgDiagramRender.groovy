package taack.ui.diagram.render

import groovy.transform.CompileStatic
import taack.ui.base.common.Style


@CompileStatic
class SvgDiagramRender implements IDiagramRender {
    private StringBuilder outStr = new StringBuilder()
    private final BigDecimal boundX
    private final BigDecimal boundY
    private BigDecimal trX = 0.0
    private BigDecimal trY = 0.0
    private Style fillStyle = Style.BOLD
    private Style strokeStyle = Style.BOLD
    private BigDecimal pxPerMmX = 72 / 25.4
    private BigDecimal pxPerMmY = 72 / 25.4
    private BigDecimal scaleX = 1.0
    private BigDecimal scaleY = 1.0
    private BigDecimal fontHeightPx = 10.0


    SvgDiagramRender(BigDecimal boundX, BigDecimal boundY) {
        this.boundX = boundX
        this.boundY = boundY
    }

    @Override
    void translateTo(BigDecimal x, BigDecimal y) {
        trX = x
        trY = y
    }

    @Override
    void fillStyle(Style style) {
        fillStyle = style
    }

    @Override
    void strokeStyle(Style style) {
        strokeStyle = style
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
        outStr.append(
                """
            <line x1="${trX * pxPerMmX * scaleX}" y1="${trY * pxPerMmY * scaleY}" x2="${toX * pxPerMmX * scaleX + trX * pxPerMmX * scaleX}" y2="${toY * pxPerMmY * scaleY + trY * pxPerMmY * scaleY}" style="stroke:black;stroke-width:1.3" />
        """.stripIndent())
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

        outStr.append("""
              <text x="${trX * pxPerMmX * scaleX}" y="${tTrY * pxPerMmY * scaleY}" text-rendering="optimizeLegibility" style="fill: black;font-size: ${fontHeightPx}px; font-family: 'sans serif'">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )

    }

    @Override
    void renderRect(String label, BigDecimal width, BigDecimal height, RectStyle style) {
        renderLabel(label)
        renderRect(width, height, style)
    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height, RectStyle style) {
        if (style == null) {
            outStr.append("""
                    <rect x="${trX * pxPerMmX * scaleX}" y="${trY * pxPerMmY * scaleY}" width="${width * pxPerMmX * scaleX}" height="${height * pxPerMmY * scaleY}" style="fill:black;" />
              """.stripIndent()
            )
        } else {
            outStr.append("""
                    <rect x="${trX * pxPerMmX * scaleX}" y="${trY * pxPerMmY * scaleY}" width="${width * pxPerMmX * scaleX}" height="${height * pxPerMmY * scaleY}" style="stroke:black;" fill-opacity="0" />
              """.stripIndent()
            )

        }
    }

    @Override
    void renderPoly(RectStyle style, BigDecimal... coords) {
        def it = coords.iterator()
        def sb = new StringBuilder()
        while (it.hasNext()) {
            sb.append(" ${(it.next() + trX) * pxPerMmX * scaleX},${(it.next() + trY) * pxPerMmY * scaleY}")
        }

        outStr.append("""
                <polygon points="$sb" style="fill:$fillStyle" />
        """.stripIndent()
        )

    }

    @Override
    void renderArrow(LineStyle style, BigDecimal... coords) {
        int headLen = 1
        def it = coords.iterator()
        def sb = new StringBuilder()
        var dx = 0.0
        var dy = 0.0
        var tdx = 0.0
        var tdy = 0.0
        var tmpTdx = 0.0
        var tmpTdy = 0.0
        sb.append(" ${trX * pxPerMmX},${trY * pxPerMmY}")
        while (it.hasNext()) {
            tdx = it.next()
            tdy = it.next()
            sb.append(" ${tdx * pxPerMmX * scaleX},${tdy * pxPerMmY * scaleY}")
            dx = tdx - tmpTdx
            dy = tdy - tmpTdy
            tmpTdx = tdx
            tmpTdy = tdy
        }
        def angle = Math.atan2(dy.toDouble(), dx.toDouble())
        sb.append(" ${(tdx - headLen * Math.cos(angle - Math.PI / 6)) * pxPerMmX},${(tdy - headLen * Math.sin(angle - Math.PI / 6)) * pxPerMmY}")
        sb.append(" ${tdx * pxPerMmX * scaleX},${tdy * pxPerMmY * scaleY}")
        sb.append(" ${(tdx - headLen * Math.cos(angle + Math.PI / 6)) * pxPerMmX},${(tdy - headLen * Math.sin(angle + Math.PI / 6)) * pxPerMmY}")
        outStr.append("""
                <polyline points="$sb" stroke="black" fill="none" />
        """.stripIndent()
        )
    }

    @Override
    BigDecimal pxInPxTransformed(BigDecimal pxY) {
        return pxY.toDouble() * 297 / 1160
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {
        def tmp = fillStyle
        fillStyle = new Style("", "black")
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
        fillStyle = tmp
    }

    String getRendered(int svgWidth, int svgHeight) {
        return """<?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="${svgWidth}px" height="${svgHeight}px" viewBox="0 0 $svgWidth $svgHeight">
            """.stripIndent() + outStr.toString() + "</svg>"
    }
}
