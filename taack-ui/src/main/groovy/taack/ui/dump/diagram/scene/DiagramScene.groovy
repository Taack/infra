package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*

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

    static KeyColor colorFrom(int i) {
        values()[i % values().size()]
    }
}

@CompileStatic
abstract class DiagramScene {
    final protected BigDecimal DIAGRAM_MARGIN_LEFT = 60.0
    final protected BigDecimal DIAGRAM_MARGIN_RIGHT = 20.0
    final protected BigDecimal DIAGRAM_MARGIN_TOP = 20.0
    final protected BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0

    protected BigDecimal fontSize
    protected BigDecimal width
    protected BigDecimal height
    protected IDiagramRender render
    protected DiagramOption diagramOption

    final protected Color BLACK_COLOR = new Color(64, 64, 64)
    final protected Color GREY_COLOR = new Color(231, 231, 231)

    static String numberToString(BigDecimal n) {
        return n.toDouble() % 1 == 0 ? "${n.toInteger()}" : "$n"
    }
}