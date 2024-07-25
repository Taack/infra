package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*

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

    final protected Color BLACK_COLOR = new Color(64, 64, 64)
    enum KeyColor {
        RED(new Color(255, 99, 132), new Color(255, 177, 193)),
        ORANGE(new Color(255, 159, 64), new Color(255, 207, 159)),
        BLUE(new Color(54, 162, 235), new Color(154, 208, 245)),
        GREEN(new Color(75, 192, 192), new Color(165, 223, 223)),
        PURPLE(new Color(153, 102, 255), new Color(204, 178, 255)),
        YELLOW(new Color(255, 205, 86), new Color(255, 230, 170)),
        GREY(new Color(201, 203, 207), new Color(228, 229, 231))

        KeyColor(Color deep, Color light) {
            this.deep = deep
            this.light = light
        }

        final Color deep
        final Color light

        static KeyColor colorFrom(int i) {
            values()[i % values().size()]
        }
    }
}