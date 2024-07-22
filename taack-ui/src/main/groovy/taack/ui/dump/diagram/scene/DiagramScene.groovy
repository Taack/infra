package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
abstract class DiagramScene {
    final protected BigDecimal LEGEND_IMAGE_WIDTH = 20.0
    final protected BigDecimal LEGEND_RECT_WIDTH = 40.0
    final protected BigDecimal LEGEND_RECT_TEXT_SPACING = 5.0
    final protected BigDecimal LEGEND_MARGIN = 10.0
    final protected BigDecimal DIAGRAM_MARGIN_LEFT = 60.0
    final protected BigDecimal DIAGRAM_MARGIN_RIGHT = 20.0
    final protected BigDecimal DIAGRAM_MARGIN_TOP = 20.0
    final protected BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0

    protected BigDecimal fontSize
    protected BigDecimal width
    protected BigDecimal height
    protected IDiagramRender render
    protected Map<String, Map<Object, BigDecimal>> dataPerKey
    protected BigDecimal diagramMarginTop = DIAGRAM_MARGIN_TOP

    enum LegendColor {
        RED(new Color(255, 99, 132), new Color(255, 177, 193)),
        ORANGE(new Color(255, 159, 64), new Color(255, 207, 159)),
        BLUE(new Color(54, 162, 235), new Color(154, 208, 245)),
        GREEN(new Color(75, 192, 192), new Color(165, 223, 223)),
        PURPLE(new Color(153, 102, 255), new Color(204, 178, 255)),
        YELLOW(new Color(255, 205, 86), new Color(255, 230, 170)),
        GREY(new Color(201, 203, 207), new Color(228, 229, 231))

        LegendColor(Color deep, Color light) {
            this.deep = deep
            this.light = light
        }

        final Color deep
        final Color light

        static LegendColor colorFrom(int i) {
            values()[i % values().size()]
        }
    }

    void drawLegend(List<String> pointImageHref = []) {
        Integer line = 1
        BigDecimal totalLength = 0.0
        Map<Integer, Map<String, BigDecimal>> keyMapPerLine = [:] // [line1: [key1: length1, key2: length2, key3: length3], line2: [...], line3: [...], ...]
        dataPerKey.keySet().eachWithIndex { String key, int i ->
            BigDecimal length = (i < pointImageHref.size() ? LEGEND_IMAGE_WIDTH : LEGEND_RECT_WIDTH) + LEGEND_RECT_TEXT_SPACING + render.measureText(key)
            if (totalLength + length > width) {
                line++
                totalLength = 0.0
            }
            if (keyMapPerLine.keySet().contains(line)) {
                keyMapPerLine[line].put(key, length)
            } else {
                Map<String, BigDecimal> m = [:]
                m.put(key, length)
                keyMapPerLine.put(line, m)
            }
            totalLength += length + LEGEND_MARGIN
        }

        diagramMarginTop += (LEGEND_MARGIN + fontSize) * line

        BigDecimal startY = LEGEND_MARGIN
        Integer legendIndex = 0
        keyMapPerLine.each {
            Map<String, BigDecimal> keyMap = it.value
            BigDecimal startX = (width - (keyMap.values().sum() as BigDecimal) - LEGEND_MARGIN * (keyMap.size() - 1)) / 2
            keyMap.each { Map.Entry<String, BigDecimal> keyEntry ->
                // image or rect, with text
                if (legendIndex < pointImageHref.size()) {
                    render.translateTo(startX, startY - (LEGEND_IMAGE_WIDTH - fontSize))
                    render.renderImage(pointImageHref[legendIndex], LEGEND_IMAGE_WIDTH, LEGEND_IMAGE_WIDTH)

                    render.translateTo(startX + LEGEND_IMAGE_WIDTH + LEGEND_RECT_TEXT_SPACING, startY)
                    render.renderLabel(keyEntry.key)
                } else {
                    render.translateTo(startX, startY)
                    LegendColor rectColor = LegendColor.colorFrom(legendIndex)
                    render.fillStyle(rectColor.light)
                    render.renderRect(LEGEND_RECT_WIDTH, fontSize, IDiagramRender.DiagramStyle.fill)
                    render.fillStyle(rectColor.deep)
                    render.renderRect(LEGEND_RECT_WIDTH, fontSize, IDiagramRender.DiagramStyle.stroke)

                    // text
                    render.translateTo(startX + LEGEND_RECT_WIDTH + LEGEND_RECT_TEXT_SPACING, startY)
                    render.renderLabel(keyEntry.key)
                }

                startX += keyEntry.value + LEGEND_MARGIN
                legendIndex++
            }
            startY += fontSize + LEGEND_MARGIN
        }
    }
}