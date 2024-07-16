package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
class AreaDiagramScene extends DiagramScene {
//    final private List<String> xLabels
//    final private boolean isStacked
//
//    private BigDecimal startLabelY
//    private BigDecimal gapY
//    private BigDecimal gapHeight
//    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
//    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
//    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0
//    final private BigDecimal MIN_GAP_WIDTH = 5.0
//
//    AreaDiagramScene(IDiagramRender render, List<String> xLabels, Map<String, List<BigDecimal>> yDataPerKey, boolean isStacked) {
//        this.fontSize = render.getFontSize()
//        this.width = render.getDiagramWidth()
//        this.height = render.getDiagramHeight()
//        this.render = render
//        this.yDataPerKey = yDataPerKey
//        this.xLabels = xLabels
//        this.isStacked = isStacked
//    }
//
//    void drawHorizontalBackground() {
//        Set<BigDecimal> values
//        Set<String> keys = yDataPerKey.keySet()
//        if (isStacked) {
//            values = []
//            for (int i = 0; i < xLabels.size(); i++) {
//                BigDecimal value = 0.0
//                for (int j = 0; j < keys.size(); j++) {
//                    value += yDataPerKey[keys[j]][i]
//                }
//                values.add(value)
//            }
//            values = values.sort() as Set<BigDecimal>
//        } else {
//            values = yDataPerKey.values().flatten().sort() as Set<BigDecimal>
//        }
//        startLabelY = values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
//        BigDecimal totalGapY = values.last() - startLabelY
//        int gapNumberY
//        if (totalGapY <= 1) {
//            gapY = 0.2
//            gapNumberY = 5
//        } else if (totalGapY <= 5) {
//            gapY = 1.0
//            gapNumberY = 5
//        } else if (totalGapY <= 10) {
//            gapY = 1.0
//            gapNumberY = 10
//        } else {
//            gapY = Math.ceil((totalGapY / 10).toDouble()).toBigDecimal()
//            gapNumberY = 10
//        }
//        BigDecimal endLabelY = startLabelY + gapY * gapNumberY
//        gapHeight = (height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM) / gapNumberY
//        render.fillStyle(new Color(231, 231, 231))
//        for (int i = 0; i <= gapNumberY; i++) {
//            // background horizontal line
//            render.translateTo(DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM, diagramMarginTop + gapHeight * i)
//            render.renderLine(width - (DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM) - DIAGRAM_MARGIN_RIGHT, 0.0)
//
//            // y axis label
//            String yLabel = "${gapY < 1 ? (endLabelY - gapY * i).round(1) : (endLabelY - gapY * i).toInteger()}"
//            render.translateTo(DIAGRAM_MARGIN_LEFT - AXIS_LABEL_MARGIN - render.measureText(yLabel), diagramMarginTop + gapHeight * i - fontSize / 2)
//            render.renderLabel(yLabel)
//        }
//    }
//
//    void drawVerticalBackground() {
//        int gapNumberX = xLabels.size() - 1
//        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
//        int showLabelEveryX = (render.measureText(xLabels.join("")) / (gapWidth * gapNumberX * 0.8)).toInteger()
//        for (int i = 0; i < gapNumberX + 1; i++) {
//            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i
//
//            // background vertical line
//            if (gapWidth >= MIN_GAP_WIDTH || i % showLabelEveryX == 0) {
//                render.translateTo(startX, diagramMarginTop)
//                render.fillStyle(new Color(231, 231, 231))
//                render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
//            }
//
//            // x axis label
//            String xLabel = xLabels[i]
//            if (showLabelEveryX >= 1) {
//                if (i % showLabelEveryX == 0) {
//                    render.translateTo(startX - render.measureText(xLabel), height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
//                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, startX, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
//                }
//            } else {
//                render.translateTo(startX - render.measureText(xLabel) / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
//                render.renderLabel(xLabel)
//            }
//        }
//    }
//
//    Map<String, List<BigDecimal>> getStackedYDataPerKey() {
//        Map<String, List<BigDecimal>> stackedYDataPerKey = new LinkedHashMap<>()
//        Set<String> keys = yDataPerKey.keySet()
//        for (int i = 0; i < keys.size(); i++) {
//            stackedYDataPerKey.put(keys[i], new ArrayList<BigDecimal>(xLabels.size()))
//            for (int j = 0; j < xLabels.size(); j++) {
//                if (i == 0) {
//                    stackedYDataPerKey[keys[i]][j] = yDataPerKey[keys[i]][j]
//                } else {
//                    stackedYDataPerKey[keys[i]][j] = stackedYDataPerKey[keys[i - 1]][j] + yDataPerKey[keys[i]][j]
//                }
//            }
//        }
//        return stackedYDataPerKey
//    }
//
//    void drawDataArea() {
//        Map<String, List<BigDecimal>> yDataPerKey = isStacked ? getStackedYDataPerKey() : yDataPerKey
//        Map<Integer, List<BigDecimal>> pointMap = new HashMap<>()
//        Set<String> keys = yDataPerKey.keySet()
//        int gapNumberX = xLabels.size() - 1
//        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / gapNumberX
//        for (int j = 0; j < keys.size(); j++) {
//            List<BigDecimal> points = new ArrayList<>()
//            for (int i = 0; i < gapNumberX + 1; i++) {
//                BigDecimal yData = yDataPerKey[keys[j]][i]
//                BigDecimal lineHeight = (yData - startLabelY) / gapY * gapHeight
//                points.add(gapWidth * i)
//                points.add(-lineHeight)
//                if (i == gapNumberX) {
//                    points.add(gapWidth * i)
//                    points.add(0.0)
//                }
//            }
//            if (isStacked && j > 0) {
//                for (int i = gapNumberX; i >= 0; i--) {
//                    BigDecimal lastLineHeight = (yDataPerKey[keys[j - 1]][i] - startLabelY) / gapY * gapHeight
//                    points.add(gapWidth * i)
//                    points.add(-lastLineHeight)
//                }
//            }
//            pointMap.put(j, points)
//        }
//        // data area
//        pointMap.forEach { Integer index, List<BigDecimal> points ->
//            Color legendColor = LegendColor.colorFrom(index)
//            render.fillStyle(new Color(legendColor.red, legendColor.green, legendColor.blue, 128))
//            render.translateTo(DIAGRAM_MARGIN_LEFT, height - DIAGRAM_MARGIN_BOTTOM)
//            render.renderPoly(points)
//        }
//
//        boolean hideInfo = gapWidth < MIN_GAP_WIDTH
//        for (int j = 0; j < keys.size(); j++) {
//            for (int i = 0; i < gapNumberX + 1; i++) {
//                BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i
//                BigDecimal yData = yDataPerKey[keys[j]][i]
//                BigDecimal lineHeight = (yData - startLabelY) / gapY * gapHeight
//                // data label
//                if (yData > startLabelY && !hideInfo) {
//                    String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
//                    render.translateTo(startX, height - DIAGRAM_MARGIN_BOTTOM - lineHeight - fontSize - 2.0)
//                    render.renderLabel(yDataLabel)
//                }
//            }
//        }
//    }
//
//    void draw() {
//        if (xLabels.isEmpty() || yDataPerKey.keySet().isEmpty()) {
//            return
//        }
//        drawLegend()
//        drawHorizontalBackground()
//        drawVerticalBackground()
//        drawDataArea()
//    }
}