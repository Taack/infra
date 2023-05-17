package taack.ui.diagram.render

import groovy.transform.CompileStatic
import taack.ui.base.common.Style

import java.awt.Color

@CompileStatic
class SvgDiagramRender implements IDiagramRender {
    private StringBuilder outStr = new StringBuilder()
    private final BigDecimal svgWidth
    private final BigDecimal svgHeight
    private BigDecimal trX = 0.0
    private BigDecimal trY = 0.0
    private String fillStyle = "black"
    private Style strokeStyle = Style.BOLD


    SvgDiagramRender(BigDecimal width, BigDecimal height) {
        this.svgWidth = width
        this.svgHeight = height
    }

    @Override
    void translateTo(BigDecimal x, BigDecimal y) {
        trX = x
        trY = y
    }

    @Override
    void fillStyle(Color color) {
        fillStyle = "rgba(${color.red}, ${color.green}, ${color.blue}, ${color.alpha / 255})"
    }

    @Override
    void strokeStyle(Style style) {

    }

    @Override
    void lineWidth(BigDecimal width) {

    }

    @Override
    void renderVerticalLine() {
        renderLine(0.0, svgHeight - trY)
    }

    @Override
    void renderHorizontalLine() {
        renderLine(svgWidth - trX, 0.0)
    }

    @Override
    void renderLine(BigDecimal toX, BigDecimal toY) {
        outStr.append(
                """
            <line x1="${trX}" y1="${trY}" x2="${toX + trX}" y2="${toY + trY}" style="stroke:${fillStyle};stroke-width:1.3" />
        """.stripIndent())
    }

    @Override
    void renderHorizontalStrip(BigDecimal height) {
        renderRect(svgWidth - trX, height)
    }

    @Override
    void renderVerticalStrip(BigDecimal width) {
        renderRect(width, svgHeight - trY)
    }

    @Override
    void renderLabel(String label) { // FONT_SIZE = 13.0
        outStr.append("""
              <text x="${trX}" y="${trY + 13.0 - 2.0}" text-rendering="optimizeLegibility" style="fill: black;font-size: ${13.0}px; font-family: 'sans serif'">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderSmallLabel(String label) {
        outStr.append("""
              <text x="$trX" y="${trY + 13.0 - 2.0}" text-rendering="optimizeLegibility" style="fill: black;font-size: ${13.0 * 0.8}px; font-family: 'sans serif'">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height) {
        outStr.append("""
                <rect x="${trX}" y="${trY}" width="${width}" height="${height}" style="fill:${fillStyle};" />
          """.stripIndent()
        )
    }

    @Override
    void renderPoly(BigDecimal... coords) {
        def it = coords.iterator()
        def sb = new StringBuilder()
        sb.append(" ${trX},${trY}")
        while (it.hasNext()) {
            sb.append(" ${it.next() + trX},${it.next() + trY}")
        }

        outStr.append("""
                <polygon points="$sb" style="fill:${fillStyle}" />
        """.stripIndent()
        )

    }

    @Override
    void renderArrow(BigDecimal... coords) { // ARROW_LENGTH = 8.0
        def it = coords.iterator()
        def sb = new StringBuilder()
        sb.append(" ${trX},${trY}")
        BigDecimal x1 = 0.0
        BigDecimal y1 = 0.0
        BigDecimal x2 = 0.0
        BigDecimal y2 = 0.0

        while (it.hasNext()) {
            x1 = x2
            y1 = y2
            x2 = it.next()
            y2 = it.next()
            sb.append(" ${trX + x2},${trY + y2}")
        }
        Double angle = Math.atan2((y2 - y1) as Double, (x2 - x1) as Double)
        sb.append(" ${trX + x2 - 8.0 * Math.cos(angle - Math.PI / 6)},${trY + (y2 - 8.0 * Math.sin(angle - Math.PI / 6))}")
        sb.append(" ${trX + x2},${trY + y2}")
        sb.append(" ${trX + x2 - 8.0 * Math.cos(angle + Math.PI / 6)},${trY + (y2 - 8.0 * Math.sin(angle + Math.PI / 6))}")
        outStr.append("""
                <polyline points="$sb" stroke="black" fill="none" />
        """.stripIndent()
        )
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {
        def tmp = fillStyle
        fillStyle = "black"
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

    String getRendered() {
        return """<?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="${svgWidth}px" height="${svgHeight}px" viewBox="0 0 $svgWidth $svgHeight">
            """.stripIndent() + outStr.toString() + "</svg>"
    }
}