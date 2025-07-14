package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
enum ElementType {
    LEGEND,
    HORIZONTAL_BACKGROUND,
    VERTICAL_BACKGROUND,
    TRANSFORM_AREA,
    DATA
}

@CompileStatic
enum KeyColor {
    RED(new Color(255, 99, 132)),
    ORANGE(new Color(255, 159, 64)),
    BLUE(new Color(54, 162, 235)),
    GREEN(new Color(75, 192, 192)),
    PURPLE(new Color(153, 102, 255)),
    YELLOW(new Color(255, 205, 86)),
    GREY(new Color(201, 203, 207))

    KeyColor(Color color) {
        this.color = color
    }

    final Color color

    static String colorToString(Color color) {
        return "rgb(${color.red}, ${color.green}, ${color.blue})"
    }
}

@CompileStatic
abstract class DiagramScene {
    protected BigDecimal DIAGRAM_MARGIN_LEFT = 60.0
    protected BigDecimal DIAGRAM_MARGIN_RIGHT = 20.0
    protected BigDecimal DIAGRAM_MARGIN_TOP = 20.0
    protected BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0
    protected BigDecimal TITLE_MARGIN = 10.0

    protected BigDecimal fontSize
    protected BigDecimal width
    protected BigDecimal height
    protected IDiagramRender render
    protected DiagramOption diagramOption
    protected BigDecimal diagramMarginTop = DIAGRAM_MARGIN_TOP

    final protected Color BLACK_COLOR = new Color(64, 64, 64)
    final protected Color GREY_COLOR = new Color(231, 231, 231)

    Color getKeyColor(int i) {
        List<Color> colors = diagramOption?.keyColors ?: KeyColor.values()*.color
        return colors[i % colors.size()]
    }

    static String numberToString(BigDecimal n) {
        return n.toDouble() % 1 == 0 ? "${n.toInteger()}" : "$n"
    }

    BigDecimal drawTitle() {
        BigDecimal height
        if (diagramOption?.title?.size() > 0) {
            render.translateTo((width - render.measureEmphasizedText(diagramOption.title)) / 2, TITLE_MARGIN)
            render.renderEmphasizedLabel(diagramOption.title)
            height = TITLE_MARGIN + (fontSize * render.EMPHASIZED_LABEL_RATE).toInteger() + TITLE_MARGIN / 2
        } else {
            height = 0.0
        }
        diagramMarginTop += height
        return height
    }
}