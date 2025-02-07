package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
abstract class RectBackgroundDiagramScene extends DiagramScene {
    final protected BigDecimal LEGEND_IMAGE_WIDTH = 19.0
    final private BigDecimal LEGEND_RECT_WIDTH = 40.0
    final private BigDecimal LEGEND_RECT_TEXT_SPACING = 5.0
    final private BigDecimal LEGEND_MARGIN = 10.0
    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0
    final private Integer GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS = 5

    private BigDecimal diagramMarginTop = DIAGRAM_MARGIN_TOP // will be increased by legend height
    protected Set<Object> xLabelList
    protected BigDecimal startLabelY
    protected BigDecimal gapY
    protected BigDecimal gapHeight
    protected Map<String, Map<Object, BigDecimal>> dataPerKey

    RectBackgroundDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, boolean canXAxisBeNumeralContinuous) {
        this.fontSize = render.getFontSize()
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.dataPerKey = dataPerKey

        this.xLabelList = []
        Set xDataList = dataPerKey.collect { it.value.keySet() }.flatten() as Set
        if (!xDataList.isEmpty()) {
            if (canXAxisBeNumeralContinuous && xDataList.every { it instanceof Number }) {
                if (xDataList.size() == 1) {
                    Integer value = xDataList.first() as Integer
                    this.xLabelList.add(value - 1)
                    this.xLabelList.add(value)
                    this.xLabelList.add(value + 1)
                } else {
                    Double min = Math.floor(xDataList.sort().first() as Double)
                    Double max = Math.ceil(xDataList.sort().last() as Double)
                    Double gap = Math.ceil(((max - min) / GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS) as Double)
                    for (int i = 0; i <= GAP_NUMBER_WHEN_CONTINUOUS_X_AXIS; i++) {
                        this.xLabelList.add((min + gap * i).toInteger())
                    }
                }
            } else {
                this.xLabelList.addAll(dataPerKey[dataPerKey.keySet().first()].keySet().collect { it.toString() })
            }
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
                render.renderGroup(["element-type": ElementType.LEGEND, "dataset": keyEntry.key, "transform": "translate(${startX},${startY})", style: "pointer-events: bounding-box;"])
                if (legendIndex < pointImageHref.size()) {
                    render.translateTo(0.0, 0.0 - (LEGEND_IMAGE_WIDTH - fontSize))
                    render.renderImage(pointImageHref[legendIndex], LEGEND_IMAGE_WIDTH, LEGEND_IMAGE_WIDTH)

                    render.translateTo(0.0 + LEGEND_IMAGE_WIDTH + LEGEND_RECT_TEXT_SPACING, 0.0)
                    render.renderLabel(keyEntry.key)
                } else {
                    render.translateTo(0.0, 0.0)
                    KeyColor rectColor = KeyColor.colorFrom(legendIndex)
                    render.fillStyle(rectColor.color)
                    render.renderRect(LEGEND_RECT_WIDTH, fontSize, IDiagramRender.DiagramStyle.fill)

                    // text
                    render.translateTo(0.0 + LEGEND_RECT_WIDTH + LEGEND_RECT_TEXT_SPACING, 0.0)
                    render.renderLabel(keyEntry.key)
                }
                render.renderGroupEnd()

                startX += keyEntry.value + LEGEND_MARGIN
                legendIndex++
            }
            startY += fontSize + LEGEND_MARGIN
        }
    }

    void drawHorizontalBackground(BigDecimal minY = null, BigDecimal maxY = null) {
        render.renderGroup(["element-type": ElementType.HORIZONTAL_BACKGROUND])
        if (minY == null || maxY == null) {
            Set<BigDecimal> values = dataPerKey.collect { it.value.values() }.flatten().sort() as Set<BigDecimal>
            minY ?= values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
            maxY ?= values.last()
        }
        startLabelY = minY
        BigDecimal totalGapY = maxY - startLabelY
        int gapNumberY
        if (totalGapY <= 1) {
            gapY = 0.2
            gapNumberY = 5
        } else if (totalGapY <= 5) {
            gapY = 1.0
            gapNumberY = 5
        } else if (totalGapY <= 10) {
            gapY = 1.0
            gapNumberY = 10
        } else {
            gapY = Math.ceil((totalGapY / 10).toDouble()).toBigDecimal()
            gapNumberY = 10
        }
        BigDecimal endLabelY = startLabelY + gapY * gapNumberY
        gapHeight = (height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM) / gapNumberY
        render.fillStyle(GREY_COLOR)
        for (int i = 0; i <= gapNumberY; i++) {
            // background horizontal line
            render.translateTo(DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM, diagramMarginTop + gapHeight * i)
            render.renderLine(width - (DIAGRAM_MARGIN_LEFT - BACKGROUND_LINE_EXCEED_DIAGRAM) - DIAGRAM_MARGIN_RIGHT, 0.0)

            // y axis label
            String yLabel = "${gapY < 1 ? (endLabelY - gapY * i).round(1) : (endLabelY - gapY * i).toInteger()}"
            render.translateTo(DIAGRAM_MARGIN_LEFT - AXIS_LABEL_MARGIN - render.measureText(yLabel), diagramMarginTop + gapHeight * i - fontSize / 2)
            render.renderLabel(yLabel)
        }
        render.renderGroupEnd()
    }

    void drawVerticalBackground(boolean isXLabelInsideGap, int showGapEveryX = 1) { // showGapEveryX: combine several gaps and only draw the content of first gap (Be used to assure enough space)
        render.renderGroup(["element-type": ElementType.VERTICAL_BACKGROUND])
        BigDecimal diagramWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
        BigDecimal gapWidth = diagramWidth / (isXLabelInsideGap ? xLabelList.size() : (xLabelList.size() > 1 ? xLabelList.size() - 1 : 1)) * showGapEveryX
        int showLabelEveryX = (render.measureText(xLabelList.join("")) / showGapEveryX / (diagramWidth * 0.8)).toInteger()
        render.fillStyle(GREY_COLOR)
        for (int i = 0; i < xLabelList.size() / showGapEveryX; i++) {
            BigDecimal startX = DIAGRAM_MARGIN_LEFT + gapWidth * i

            // background vertical line
            render.translateTo(startX, diagramMarginTop)
            render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))

            // x axis label
            BigDecimal xOffset = isXLabelInsideGap ? gapWidth / 2 : 0
            String xLabel = xLabelList[i * showGapEveryX]
            if (showLabelEveryX > 0) {
                if (i % showLabelEveryX == 0) {
                    render.translateTo(startX - render.measureText(xLabel) + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, startX + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                }
            } else {
                render.translateTo(startX - render.measureText(xLabel) / 2 + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                render.renderLabel(xLabel)
            }
        }
        // add last background vertical line
        if (isXLabelInsideGap) {
            render.translateTo(DIAGRAM_MARGIN_LEFT + gapWidth * (xLabelList.size() / showGapEveryX).toInteger(), diagramMarginTop)
            render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
        }
        render.renderGroupEnd()
    }

    void buildTransformAreaStart(String shapeType, BigDecimal shapeMaxWidth = 0.0) {
        String id = "clipSection"
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [DIAGRAM_MARGIN_LEFT - 1, 0.0,
                                  width - DIAGRAM_MARGIN_RIGHT + 1, 0.0,
                                  width - DIAGRAM_MARGIN_RIGHT + 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  width, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  width, height,
                                  DIAGRAM_MARGIN_LEFT / 2, height,
                                  DIAGRAM_MARGIN_LEFT / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  DIAGRAM_MARGIN_LEFT - 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN])

        render.renderGroup(["clip-path": "url(#${id})"])
        render.renderGroup(["element-type": ElementType.TRANSFORM_AREA, "shape-type": shapeType, "shape-max-width": shapeMaxWidth, "area-min-x": DIAGRAM_MARGIN_LEFT, "area-max-x": width - DIAGRAM_MARGIN_RIGHT, "area-max-y": height - DIAGRAM_MARGIN_BOTTOM])
    }

    void buildTransformAreaEnd() {
        render.renderGroupEnd()
        render.renderGroupEnd()
    }
}