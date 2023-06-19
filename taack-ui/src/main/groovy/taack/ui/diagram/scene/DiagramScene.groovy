package taack.ui.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.diagram.render.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
abstract class DiagramScene {
    protected BigDecimal width
    protected BigDecimal height
    protected IDiagramRender render
    protected Map<String, List<BigDecimal>> yDataPerKey

    final protected BigDecimal LEGEND_MARGIN = 10.0
    final protected BigDecimal LEGEND_RECT_WIDTH = 40.0
    final protected BigDecimal LEGEND_RECT_HEIGHT = 13.0 // same as font size
    final protected BigDecimal DIAGRAM_MARGIN_LEFT = 60.0
    final protected BigDecimal DIAGRAM_MARGIN_RIGHT = 20.0
    final protected BigDecimal DIAGRAM_MARGIN_TOP = 10.0
    final protected BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0

    enum LegendColor {
        RED(new Color(255, 99, 132)),
        ORANGE(new Color(255, 159, 64)),
        BLUE(new Color(54, 162, 235)),
        GREEN(new Color(75, 192, 192)),
        PURPLE(new Color(153, 102, 255)),
        YELLOW(new Color(255, 205, 86)),
        GREY(new Color(201, 203, 207))

        LegendColor(Color color) {
            this.color = color
        }

        final Color color

        static Color colorFrom(int i) {
            values()[i % values().size()].color
        }
    }

    void drawLegend() {
        Set<String> keys = yDataPerKey.keySet()
        BigDecimal legendX = (width - keys.size() * (LEGEND_RECT_WIDTH + 5.0 + LEGEND_MARGIN) + LEGEND_MARGIN - render.measureText(keys.join(""))) / 2
        keys.eachWithIndex { String key, int index ->
            // rect
            render.translateTo(legendX, LEGEND_MARGIN)
            Color rectColor = LegendColor.colorFrom(index)
            render.fillStyle(new Color(rectColor.red, rectColor.green, rectColor.blue, 128))
            render.renderRect(LEGEND_RECT_WIDTH, LEGEND_RECT_HEIGHT, IDiagramRender.RectStyle.fill)
            render.fillStyle(rectColor)
            render.renderRect(LEGEND_RECT_WIDTH, LEGEND_RECT_HEIGHT, IDiagramRender.RectStyle.stroke)

            // text
            render.translateTo(legendX + LEGEND_RECT_WIDTH + 5.0, LEGEND_MARGIN)
            render.renderLabel(key)

            legendX += LEGEND_RECT_WIDTH + 5.0 + render.measureText(key) + LEGEND_MARGIN
        }
    }
}