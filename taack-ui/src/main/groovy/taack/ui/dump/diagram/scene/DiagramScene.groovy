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
    DATA_CONTAINER,
    DATA
}

@CompileStatic
enum KeyColor {
    RED(new Color(255, 99, 132)),
    ORANGE(new Color(255, 172, 90)),
    BLUE(new Color(54, 162, 235)),
    GREEN(new Color(72, 192, 114)),
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
    protected BigDecimal DIAGRAM_MARGIN_TOP = 20.0
    protected BigDecimal DIAGRAM_MARGIN_BOTTOM = 60.0
    protected BigDecimal DIAGRAM_Y_AXIS_WIDTH = 40.0
    protected BigDecimal TITLE_MARGIN = 10.0

    public IDiagramRender render
    protected BigDecimal fontSize
    protected DiagramOption diagramOption
    protected BigDecimal diagramMarginTop // may be enlarged by Title and Legends
    protected BigDecimal diagramMarginLeft // may be enlarged by multiple y axes
    protected BigDecimal diagramMarginRight // may be enlarged by multiple y axes
    protected Integer comboCurrentCount
    protected Integer comboTotalCount
    protected boolean alwaysShowFullInfo

    final protected Color BLACK_COLOR = new Color(64, 64, 64)
    final protected Color GREY_COLOR = new Color(231, 231, 231)

    private NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.locale)

    DiagramScene(IDiagramRender render, DiagramOption diagramOption) {
        this.render = render
        this.fontSize = render.getFontSize()
        this.diagramOption = diagramOption

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            DIAGRAM_MARGIN_TOP *= rate
            DIAGRAM_MARGIN_BOTTOM *= rate
            DIAGRAM_Y_AXIS_WIDTH *= rate
            TITLE_MARGIN *= rate
        }
    }

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

    boolean isMainDiagram() {
        return comboCurrentCount > comboTotalCount
    }

    BigDecimal drawTitle() {
        BigDecimal height
        if (diagramOption?.title?.size() > 0) {
            if (isMainDiagram()) {
                render.translateTo((render.getDiagramWidth() - render.measureEmphasizedText(diagramOption.title)) / 2, TITLE_MARGIN)
                render.renderEmphasizedLabel(diagramOption.title)
            }
            height = TITLE_MARGIN + (fontSize * render.EMPHASIZED_LABEL_RATE).toInteger() + TITLE_MARGIN / 2
        } else {
            height = 0.0
        }
        diagramMarginTop += height
        return height
    }

    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        this.alwaysShowFullInfo = alwaysShowFullInfo
        this.diagramMarginTop = DIAGRAM_MARGIN_TOP
        this.comboTotalCount = comboTotalCount
        this.diagramMarginLeft = DIAGRAM_Y_AXIS_WIDTH * (Math.floor(comboTotalCount.toDouble() / 2) + 1)
        this.diagramMarginRight = DIAGRAM_Y_AXIS_WIDTH * (comboTotalCount == 0 ? 1 : Math.ceil(comboTotalCount.toDouble() / 2))
        this.comboCurrentCount = comboCurrentCount
    }
}