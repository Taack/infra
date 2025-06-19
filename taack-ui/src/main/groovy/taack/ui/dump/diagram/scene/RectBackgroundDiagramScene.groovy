package taack.ui.dump.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramXLabelDateFormat
import taack.ui.dump.diagram.scene.DiagramScene
import taack.ui.dump.diagram.scene.ElementType
import taack.ui.dump.diagram.scene.KeyColor

@CompileStatic
abstract class RectBackgroundDiagramScene extends DiagramScene {
    final protected BigDecimal LEGEND_IMAGE_WIDTH = 19.0
    final private BigDecimal LEGEND_RECT_WIDTH = 40.0
    final private BigDecimal LEGEND_RECT_TEXT_SPACING = 5.0
    final private BigDecimal LEGEND_MARGIN = 10.0
    final private BigDecimal BACKGROUND_LINE_EXCEED_DIAGRAM = 5.0
    final private BigDecimal AXIS_LABEL_MARGIN = 10.0
    final private BigDecimal LABEL_ROTATE_ANGLE_WHEN_MASSIVE = -20.0
    final protected BigDecimal MIN_GAP_WIDTH = 5.0

    private BigDecimal diagramMarginTop = DIAGRAM_MARGIN_TOP // will be increased by legend height
    protected Set<Object> xLabelList = []
    protected BigDecimal startLabelY
    protected BigDecimal gapY
    protected BigDecimal gapHeight
    protected Map<String, Map<Object, BigDecimal>> dataPerKey
    protected boolean alwaysShowFullInfo = false
    protected boolean isXLabelInsideGap = false
    protected DiagramXLabelDateFormat xLabelDateFormat = DiagramXLabelDateFormat.DAY

    BigDecimal getGreatestCommonDivisor(BigDecimal a, BigDecimal b) {
        if (a < b) {
            return getGreatestCommonDivisor(b, a)
        }
        if (b < 0.001) {
            return a
        } else {
            return getGreatestCommonDivisor(b, a - (a / b).toInteger() * b)
        }
    }

    RectBackgroundDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        this.fontSize = render.getFontSize()
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.dataPerKey = dataPerKey
    }

    boolean buildXLabelList() {
        Set<Object> xDataList = dataPerKey.collect { it.value.keySet() }.flatten().unique() as Set<Object>
        if (xDataList.isEmpty()) {
            return false
        }
        if (xDataList.every { it instanceof Number }) { // continuous X axis (Numeral)
            xDataList = xDataList.sort() as Set<Object>
            if (isXLabelInsideGap) { // bar/whiskers: should exactly have related gap for every X data
                if (xDataList.size() > 1) {
                    BigDecimal gap = (xDataList[1] as BigDecimal) - (xDataList[0] as BigDecimal)
                    for (int i = 2; i < xDataList.size(); i++) {
                        gap = getGreatestCommonDivisor(gap, (xDataList[i] as BigDecimal) - (xDataList[i - 1] as BigDecimal))
                    }
                    BigDecimal min = Math.floor(xDataList.first() as Double).toBigDecimal()
                    BigDecimal max = Math.ceil(xDataList.last() as Double).toBigDecimal()
                    BigDecimal label = min
                    while (label <= max) {
                        this.xLabelList.add(label)
                        label += gap
                    }
                } else {
                    this.xLabelList = xDataList
                }
            } else { // scatter/line/area: no matter how X axis looks
                if (xDataList.size() > 1) {
                    int gapNumber = 5
                    Double min = Math.floor(xDataList.first() as Double)
                    Double max = Math.ceil(xDataList.last() as Double)
                    Double gap = Math.ceil(((max - min) / gapNumber) as Double)
                    Set labels = []
                    for (int i = 0; i <= gapNumber; i++) {
                        labels.add((min + gap * i).toInteger())
                    }
                    this.xLabelList = labels
                } else {
                    Integer value = xDataList.first() as Integer
                    this.xLabelList = (value > 0 ? [value - 1, value, value + 1] : [0, 1, 2]) as Set<Object>
                }
            }
        } else if (xDataList.every { it instanceof Date }) { // continuous X axis (Date)
            xDataList = xDataList.sort() as Set<Object>
            Calendar cal = Calendar.getInstance()
            cal.setTime(xDataList.last() as Date)
            cal.set(xLabelDateFormat.subUnit, cal.getActualMaximum(xLabelDateFormat.subUnit))
            if (!isXLabelInsideGap) {
                cal.add(xLabelDateFormat.unit, 1)
            }
            Date dateMax = cal.getTime()

            cal.setTime(xDataList.first() as Date)
            cal.set(xLabelDateFormat.subUnit, cal.getActualMinimum(xLabelDateFormat.subUnit))
            Date dateMin = cal.getTime()

            while (dateMin.before(dateMax)) {
                this.xLabelList.add(dateMin)
                cal.add(xLabelDateFormat.unit, 1)
                dateMin = cal.getTime()
            }
        } else {  // discrete X axis
            this.xLabelList = xDataList.collect { it.toString() } as Set<Object>
        }
        return true
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
                render.renderGroup(['element-type': ElementType.LEGEND, 'dataset': keyEntry.key, 'transform': "translate(${startX},${startY})', style: 'pointer-events: bounding-box;"])
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
        render.renderGroup(['element-type': ElementType.HORIZONTAL_BACKGROUND])
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

    static BigDecimal objectToNumber(Object o) {
        return o instanceof Date ? o.getTime() : o instanceof Number ? o : 0.0
    }

    void drawVerticalBackground(int showGapEveryX = 1) { // showGapEveryX: combine several gaps and only draw the content of first gap (Be used to assure enough space)
        int displayedXLabelListNumber = (xLabelList.size() / showGapEveryX).toInteger()
        BigDecimal diagramWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
        BigDecimal gapWidth = diagramWidth / (isXLabelInsideGap ? displayedXLabelListNumber : (displayedXLabelListNumber > 1 ? displayedXLabelListNumber - 1 : 1))
        boolean isDate = xLabelList.every { it instanceof Date }
        BigDecimal xLabelTotalLength = render.measureText(xLabelList.collect { isDate ? xLabelDateFormat.format(it as Date) : it.toString() }.join(''))
        int showLabelEveryX = Math.ceil((xLabelTotalLength / showGapEveryX / (diagramWidth * 0.8)).toDouble()).toInteger()

        render.renderGroup(['element-type': ElementType.VERTICAL_BACKGROUND, 'show-label-every-x': xLabelTotalLength / showGapEveryX / (diagramWidth * 0.8)])
        render.fillStyle(GREY_COLOR)
        BigDecimal minX = objectToNumber(xLabelList.first())
        BigDecimal maxX = objectToNumber(xLabelList.last())
        for (int i = 0; i < displayedXLabelListNumber; i++) {
            BigDecimal coordX
            if (isDate && !isXLabelInsideGap) { // X axis is of type Date, so each gap may have different width (For example, each month has different dayNumber)
                BigDecimal x = objectToNumber(xLabelList[i])
                coordX = DIAGRAM_MARGIN_LEFT + (x - minX) / (maxX - minX) * diagramWidth
            } else {
                coordX = DIAGRAM_MARGIN_LEFT + gapWidth * i
            }

            // background vertical line
            if (alwaysShowFullInfo || gapWidth >= MIN_GAP_WIDTH || i % showLabelEveryX == 0) {
                render.translateTo(coordX, diagramMarginTop)
                render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
            }

            // x axis label
            BigDecimal xOffset = isXLabelInsideGap ? gapWidth / 2 : 0
            String xLabel = isDate ? xLabelDateFormat.format(xLabelList[i * showGapEveryX] as Date) : xLabelList[i * showGapEveryX].toString()
            BigDecimal labelLength = render.measureText(xLabel)
            if (gapWidth >= labelLength) {
                render.translateTo(coordX - labelLength / 2 + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                if (i % showLabelEveryX == 0) {
                    render.renderLabel(xLabel)
                } else if (alwaysShowFullInfo) {
                    render.renderHiddenLabel(xLabel)
                }
            } else {
                render.translateTo(coordX - labelLength + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                if (i % showLabelEveryX == 0) {
                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, coordX + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                } else if (alwaysShowFullInfo) {
                    render.renderHiddenRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, coordX + xOffset, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                }
            }
        }
        if (isXLabelInsideGap) {
            render.translateTo(width - DIAGRAM_MARGIN_RIGHT, diagramMarginTop)
            render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
        }
        render.renderGroupEnd()
    }

    void buildTransformAreaStart(String shapeType, String diagramActionUrl = null, BigDecimal shapeMaxWidth = 0.0) {
        String id = 'clipSection'
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [DIAGRAM_MARGIN_LEFT - 1, 0.0,
                                  width - DIAGRAM_MARGIN_RIGHT + 1, 0.0,
                                  width - DIAGRAM_MARGIN_RIGHT + 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  width, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  width, height,
                                  DIAGRAM_MARGIN_LEFT / 2, height,
                                  DIAGRAM_MARGIN_LEFT / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                  DIAGRAM_MARGIN_LEFT - 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN])

        render.renderGroup(['clip-path': "url(#${id})"])
        render.renderGroup(['element-type': ElementType.TRANSFORM_AREA,
                            'diagram-action-url': diagramActionUrl ?: '',
                            'shape-type': shapeType,
                            'shape-max-width': shapeMaxWidth,
                            'area-min-x': DIAGRAM_MARGIN_LEFT,
                            'area-max-x': width - DIAGRAM_MARGIN_RIGHT,
                            'area-max-y': height - DIAGRAM_MARGIN_BOTTOM])
    }

    void buildTransformAreaEnd() {
        render.renderGroupEnd()
        render.renderGroupEnd()
    }

    void setXLabelDateFormat(DiagramXLabelDateFormat xLabelDateFormat) {
        this.xLabelDateFormat = xLabelDateFormat
    }
}