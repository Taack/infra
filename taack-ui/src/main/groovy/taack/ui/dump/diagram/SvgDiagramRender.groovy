package taack.ui.dump.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage

@CompileStatic
class SvgDiagramRender implements IDiagramRender {
    private StringBuilder outStr = new StringBuilder()
    private final BigDecimal svgWidth
    private final BigDecimal svgHeight
    private final boolean isViewBox
    private final BigDecimal fontSizePercentage
    private final FontMetrics fm
    private Integer fontSize = 13
    private BigDecimal trX = 0.0
    private BigDecimal trY = 0.0
    private String fillStyle = 'black'
    private BigDecimal lineWidth = 1.3

    private BigDecimal LABEL_MARGIN = 2.0
    private BigDecimal ARROW_LENGTH = 8.0

    // if isViewBox == true, the diagramWidth/diagramHeight will be auto-fit (It always equals to 100%), and the params 'width/height' will be used to do ZOOM
    // if isViewBox == false, the diagramWidth/diagramHeight will be fixed to params 'width/height'
    SvgDiagramRender(DiagramOption.DiagramResolution resolution, boolean isViewBox = false) {
        this.svgWidth = resolution.width
        this.svgHeight = resolution.height
        this.isViewBox = isViewBox
        this.fontSizePercentage = resolution.fontSizePercentage
        this.fontSize = (this.fontSize * fontSizePercentage).toInteger()
        this.lineWidth *= fontSizePercentage
        this.fm = new BufferedImage(svgWidth.toInteger(), svgHeight.toInteger(), BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize))

        this.LABEL_MARGIN *= fontSizePercentage
        this.ARROW_LENGTH *= fontSizePercentage
    }

    SvgDiagramRender(BigDecimal width, BigDecimal height, boolean isViewBox = false, BigDecimal fontSizePercentage = 1.0) {
        this.svgWidth = width
        this.svgHeight = height
        this.isViewBox = isViewBox
        this.fontSizePercentage = fontSizePercentage
        this.fontSize = (this.fontSize * fontSizePercentage).toInteger()
        this.lineWidth *= fontSizePercentage
        this.fm = new BufferedImage(svgWidth.toInteger(), svgHeight.toInteger(), BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize))

        this.LABEL_MARGIN *= fontSizePercentage
        this.ARROW_LENGTH *= fontSizePercentage
    }

    @Override
    void translateTo(BigDecimal x, BigDecimal y) {
        trX = x
        trY = y
    }

    @Override
    void fillStyle(Color color) {
        fillStyle = "rgb(${color.red}, ${color.green}, ${color.blue})"
    }

    @Override
    void lineWidth(BigDecimal width) {
        lineWidth = width
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
        outStr.append("""
            <line x1="${trX}" y1="${trY}" x2="${toX + trX}" y2="${toY + trY}" style="stroke:${fillStyle};stroke-width:${lineWidth};" />
        """.stripIndent())
    }

    @Override
    void renderHiddenLine(BigDecimal toX, BigDecimal toY) {
        outStr.append("""
                <line x1="${trX}" y1="${trY}" x2="${toX + trX}" y2="${toY + trY}" style="stroke:${fillStyle};stroke-width:${lineWidth};display: none;" />
        """.stripIndent()
        )
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
    void renderLabel(String label) {
        outStr.append("""
              <text x="${trX}" y="${trY + fontSize - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${fontSize}px; font-family: sans-serif; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderHiddenLabel(String label) {
        outStr.append("""
              <text x="${trX}" y="${trY + fontSize - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${fontSize}px; font-family: sans-serif; display: none; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderSmallLabel(String label) {
        outStr.append("""
              <text x="$trX" y="${trY + (fontSize * SMALL_LABEL_RATE).toInteger() - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${(fontSize * SMALL_LABEL_RATE).toInteger()}px; font-family: sans-serif; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY) {
        outStr.append("""
              <text transform="rotate($rotateAngle,$rotatePointX,$rotatePointY)" x="$trX" y="${trY + fontSize - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${fontSize}px; font-family: sans-serif; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderHiddenRotatedLabel(String label, BigDecimal rotateAngle, BigDecimal rotatePointX, BigDecimal rotatePointY) {
        outStr.append("""
              <text transform="rotate($rotateAngle,$rotatePointX,$rotatePointY)" x="$trX" y="${trY + fontSize - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${fontSize}px; font-family: sans-serif; display: none; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderEmphasizedLabel(String label) {
        outStr.append("""
              <text x="${trX}" y="${trY + (fontSize * EMPHASIZED_LABEL_RATE).toInteger() - LABEL_MARGIN}" label-width="${measureText(label)}" text-rendering="optimizeLegibility" style="font-size: ${(fontSize * EMPHASIZED_LABEL_RATE).toInteger()}px; font-family: sans-serif; font-weight: bold; pointer-events: none;">${label.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")}</text>
        """.stripIndent()
        )
    }

    @Override
    void renderRect(BigDecimal width, BigDecimal height, DiagramStyle diagramStyle = DiagramStyle.fill) {
        if (diagramStyle == DiagramStyle.fill) {
            outStr.append("""
                <rect x="${trX}" y="${trY}" width="${width}" height="${height}" style="fill:${fillStyle};" />
          """.stripIndent()
            )
        } else {
            outStr.append("""
                <rect x="${trX}" y="${trY}" width="${width}" height="${height}" style="stroke:${fillStyle};" stroke-width="${lineWidth}" fill-opacity="0" />
          """.stripIndent()
            )
        }
    }

    @Override
    void renderCircle(BigDecimal radius, DiagramStyle diagramStyle = DiagramStyle.fill) {
        if (diagramStyle == DiagramStyle.fill) {
            outStr.append("""
                <circle cx="${trX}" cy="${trY}" r="${radius}" fill="${fillStyle}" />
          """.stripIndent()
            )
        } else {
            outStr.append("""
                <circle cx="${trX}" cy="${trY}" r="${radius}" stroke="${fillStyle}" stroke-width="${lineWidth}" fill="none" />
          """.stripIndent()
            )
        }
    }

    @Override
    void renderPoly(List<BigDecimal> coords, DiagramStyle diagramStyle = DiagramStyle.fill) {
        def it = coords.iterator()
        def sb = new StringBuilder()
        while (it.hasNext()) {
            sb.append(" ${it.next() + trX},${it.next() + trY}")
        }

        if (diagramStyle == DiagramStyle.fill) {
            outStr.append("""
                <polygon points="$sb" style="fill:${fillStyle}" />
            """.stripIndent()
            )
        } else {
            outStr.append("""
                <polygon points="$sb" stroke="${fillStyle}" stroke-width="${lineWidth}" fill="none" />
            """.stripIndent()
            )
        }
    }

    @Override
    void renderArrow(BigDecimal... coords) {
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
        sb.append(" ${trX + x2 - ARROW_LENGTH * Math.cos(angle - Math.PI / 6)},${trY + (y2 - ARROW_LENGTH * Math.sin(angle - Math.PI / 6))}")
        sb.append(" ${trX + x2},${trY + y2}")
        sb.append(" ${trX + x2 - ARROW_LENGTH * Math.cos(angle + Math.PI / 6)},${trY + (y2 - ARROW_LENGTH * Math.sin(angle + Math.PI / 6))}")
        outStr.append("""
                <polyline points="$sb" stroke="black" fill="none" />
        """.stripIndent()
        )
    }

    @Override
    void renderTriangle(BigDecimal length, boolean isDown) {
        String tmpStyle = fillStyle
        fillStyle = 'black'
        if (isDown) {
            renderPoly(
                    [0.0, 0.0,
                    length, 0.0,
                    length / 2.0, length]
            )
        } else {
            renderPoly(
                    [0.0, 0.0,
                    length, length / 2.0,
                    0.0, length]
            )
        }
        fillStyle = tmpStyle
    }

    @Override
    void renderSector(BigDecimal r, BigDecimal angle1, BigDecimal angle2, DiagramStyle diagramStyle = DiagramStyle.fill) {
        if (angle1 == 0.0 && angle2 == 360.0) {
            renderCircle(r, diagramStyle)
            return
        }

        Double radius = r as Double
        Double centerX = trX as Double
        Double centerY = trY as Double
        Double startAngle = Math.toRadians(angle1.toDouble())
        Double endAngle = Math.toRadians(angle2.toDouble())

        Double startX = centerX + radius * Math.cos(startAngle - Math.PI / 2)
        Double startY = centerY + radius * Math.sin(startAngle - Math.PI / 2)
        Double endX = centerX + radius * Math.cos(endAngle - Math.PI / 2)
        Double endY = centerY + radius * Math.sin(endAngle - Math.PI / 2)
        int largeArcFlag = angle2 - angle1 <= 180.0 ? 0 : 1
        if (diagramStyle == DiagramStyle.fill) {
            outStr.append("""
                <path d="M ${trX} ${trY} L ${startX} ${startY} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${endX} ${endY} Z" fill="${fillStyle}" />
          """.stripIndent()
            )
        } else {
            outStr.append("""
                <path d="M ${trX} ${trY} L ${startX} ${startY} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${endX} ${endY} Z" fill="transparent" stroke="${fillStyle}" stroke-width="${lineWidth}" />
          """.stripIndent()
            )
        }
    }

    @Override
    void renderImage(String filepath, BigDecimal width, BigDecimal height) {
        outStr.append("""
                <image x="${trX}" y="${trY}" href="${filepath}" width="${width}" height="${height}" />
        """.stripIndent()
        )
    }

    @Override
    void renderGroup(Map attributes) {
        outStr.append("""
                <g ${attributes.collect { "${it.key}=\"${it.value}\"" }.join(" ")}>
        """.stripIndent()
        )
    }

    @Override
    void renderGroupEnd() {
        outStr.append("""
                </g>
        """.stripIndent()
        )
    }

    @Override
    void renderClipSection(String id, List<BigDecimal> coords) {
        outStr.append("""<clipPath id="${id}">""")
        renderPoly(coords)
        outStr.append('</clipPath>')
    }

    @Override
    BigDecimal getFontSize() {
        return fontSize
    }

    @Override
    BigDecimal getDiagramWidth() {
        return svgWidth
    }

    @Override
    BigDecimal getDiagramHeight() {
        return svgHeight
    }

    @Override
    BigDecimal measureText(String text) {
        return fm.stringWidth(text)
    }

    @Override
    BigDecimal measureSmallText(String text) {
        return new BufferedImage(svgWidth.toInteger(), svgHeight.toInteger(), BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (fontSize * SMALL_LABEL_RATE).toInteger())).stringWidth(text)
    }

    @Override
    BigDecimal measureEmphasizedText(String text) {
        return new BufferedImage(svgWidth.toInteger(), svgHeight.toInteger(), BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(new Font(Font.SANS_SERIF, Font.BOLD, (fontSize * EMPHASIZED_LABEL_RATE).toInteger())).stringWidth(text)
    }

    String getRendered() {
        return """<?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
                class="taackDiagram" font-size-percentage="${fontSizePercentage}"
                ${isViewBox ? "viewBox='0 0 $svgWidth $svgHeight'" : "width='${svgWidth}px' height='${svgHeight}px'"}>
            """.stripIndent() + outStr.toString() + "</svg>"
    }
}