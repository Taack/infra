package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import org.springframework.context.i18n.LocaleContextHolder
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color
import java.text.NumberFormat

@CompileStatic
enum ElementType {
    LEGEND,
    HORIZONTAL_BACKGROUND,
    VERTICAL_BACKGROUND,
    VERTICAL_SCROLL_BAR,
    TOOLTIP,
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
    protected IDiagramRender render
    protected DiagramOption diagramOption
    protected BigDecimal diagramMarginTop = DIAGRAM_MARGIN_TOP

    final protected Color BLACK_COLOR = new Color(64, 64, 64)
    final protected Color GREY_COLOR = new Color(231, 231, 231)

    private NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.locale)

    String numberToString(BigDecimal n) {
        return nf.format(n)
    }

    Color getKeyColor(int i) {
        if (i >= 0) {
            List<Color> colors = diagramOption?.keyColors ?: KeyColor.values()*.color
            return colors[i % colors.size()]
        } else {
            return GREY_COLOR
        }
    }

    BigDecimal drawTitle() {
        BigDecimal height
        if (diagramOption?.title?.size() > 0) {
            render.translateTo((render.getDiagramWidth() - render.measureEmphasizedText(diagramOption.title)) / 2, TITLE_MARGIN)
            render.renderEmphasizedLabel(diagramOption.title)
            height = TITLE_MARGIN + (fontSize * render.EMPHASIZED_LABEL_RATE).toInteger() + TITLE_MARGIN / 2
        } else {
            height = 0.0
        }
        diagramMarginTop += height
        return height
    }

    void draw(boolean alwaysShowFullInfo = false) {

    }
}