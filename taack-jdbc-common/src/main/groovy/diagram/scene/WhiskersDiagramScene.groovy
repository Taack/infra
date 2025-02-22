package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class WhiskersDiagramScene extends RectBackgroundDiagramScene {
    final private BigDecimal MIN_BOX_WIDTH = 5.0
    final private BigDecimal MAX_BOX_WIDTH = 40.0
    final private BigDecimal BOX_LINE_WIDTH = 2.0

    final private Map<String, List<List<BigDecimal>>> yDataListPerKey

    WhiskersDiagramScene(IDiagramRender render, Object[] xDataList, Map<String, List<List<BigDecimal>>> yDataListPerKey, String diagramActionUrl = null, boolean alwaysShowFullInfo = false) {
        super(render, yDataListPerKey.collectEntries { [(it.key): xDataList.collectEntries { xData -> [(xData): 0.0] }] } as Map<String, Map<Object, BigDecimal>>, false)
        this.diagramActionUrl = diagramActionUrl
        this.alwaysShowFullInfo = alwaysShowFullInfo
        this.yDataListPerKey = yDataListPerKey
    }

    void drawHorizontalBackground() {
        Set<BigDecimal> values = yDataListPerKey.values().flatten().sort() as Set<BigDecimal>
        if (!values.isEmpty()) {
            BigDecimal minY = values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
            BigDecimal maxY = values.last()
            super.drawHorizontalBackground(minY, maxY)
        } else {
            super.drawHorizontalBackground(0.0, 0.0)
        }
    }

    static BigDecimal findMedian(List<BigDecimal> boxData, int begin, int end) {
        int count = end - begin
        if (count % 2 == 0) {
            BigDecimal top = boxData[count / 2 + begin]
            BigDecimal low = boxData[count / 2 - 1 + begin]
            return (top + low) / 2
        } else {
            return boxData[(count / 2).toInteger() + begin]
        }
    }

    void drawVerticalBackgroundAndDataWhiskersBox() {
        Set<String> keys = yDataListPerKey.keySet()
        int showGapEveryX = 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / xLabelList.size()
        if (!alwaysShowFullInfo) {
            // box width should be bigger than a min value (In the case of "smaller", we will combine several gaps to get enough space and only draw boxes of the first gap. It means "showGapEveryX".)
            int boxNumber = keys.size()
            BigDecimal singleBoxWidth = boxNumber > 1 ? (gapWidth * 0.8) * 0.8 / boxNumber : gapWidth * 0.8
            if (singleBoxWidth < MIN_BOX_WIDTH) {
                BigDecimal minGapWidth = boxNumber > 1 ? MIN_BOX_WIDTH * boxNumber / 0.8 / 0.8 : MIN_BOX_WIDTH / 0.8
                showGapEveryX = Math.ceil((minGapWidth / gapWidth).toDouble()).toInteger()
                gapWidth = gapWidth * showGapEveryX
            }
        }
        super.drawVerticalBackground(true, showGapEveryX)

        // calculate true value of box width
        BigDecimal gapHorizontalPadding = gapWidth * 0.2 / 2
        int boxNumber = keys.size()
        BigDecimal boxWidth = boxNumber > 1 ? (gapWidth * 0.8) * 0.8 / boxNumber : gapWidth * 0.8
        BigDecimal boxMargin = boxNumber > 1 ? (gapWidth * 0.8) * 0.2 / (boxNumber - 1) : 0.0
        if (boxWidth > MAX_BOX_WIDTH) {
            boxWidth = MAX_BOX_WIDTH
            gapHorizontalPadding = (gapWidth - boxWidth * boxNumber - boxMargin * (boxNumber - 1)) / 2
        }

        // data whiskers box
        render.lineWidth(BOX_LINE_WIDTH)
        for (int i = 0; i < (xLabelList.size() / showGapEveryX).toInteger(); i++) {
            BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + gapWidth * i + gapHorizontalPadding
            for (int j = 0; j < keys.size(); j++) {
                List<List<BigDecimal>> yDataList = yDataListPerKey[keys[j]]
                List<BigDecimal> boxData = i * showGapEveryX < yDataList.size() ? yDataList[i * showGapEveryX].sort() : []
                int size = boxData.size()
                if (size > 0) {
                    BigDecimal lowerExtreme = boxData.first()
                    BigDecimal lowerQuartile = findMedian(boxData, 0, (size / 2).toInteger())
                    BigDecimal median = findMedian(boxData, 0, size)
                    BigDecimal upperQuartile = findMedian(boxData, (size / 2).toInteger() + size % 2, size)
                    BigDecimal upperExtreme = boxData.last()
                    if (lowerExtreme > startLabelY) {
                        render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i, "data-x": xLabelList[i], "data-y": upperExtreme, "data-label": "${xLabelList[i]}: ${upperExtreme}", style: "pointer-events: bounding-box;"])
                        // line from upperExtreme to upperQuartile
                        render.translateTo(xWidth + boxWidth / 2, height - DIAGRAM_MARGIN_BOTTOM - (upperExtreme - startLabelY) / gapY * gapHeight)
                        render.fillStyle(BLACK_COLOR)
                        render.renderLine(0.0, (upperExtreme - upperQuartile) / gapY * gapHeight)
                        // upperExtreme line
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (upperExtreme - startLabelY) / gapY * gapHeight)
                        render.renderLine(boxWidth, 0.0)
                        render.renderGroupEnd()

                        render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i, "data-x": xLabelList[i], "data-y": upperQuartile, "data-label": "${xLabelList[i]}: ${upperQuartile}"])
                        // rect from upperQuartile to median
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (upperQuartile - startLabelY) / gapY * gapHeight)
                        render.fillStyle(KeyColor.colorFrom(j).color)
                        render.renderRect(boxWidth, (upperQuartile - median) / gapY * gapHeight, IDiagramRender.DiagramStyle.fill)
                        render.fillStyle(BLACK_COLOR)
                        render.renderRect(boxWidth, (upperQuartile - median) / gapY * gapHeight, IDiagramRender.DiagramStyle.stroke)
                        render.renderGroupEnd()

                        render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i, "data-x": xLabelList[i], "data-y": median, "data-label": "${xLabelList[i]}: ${median}"])
                        // rect from median to lowerQuartile
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (median - startLabelY) / gapY * gapHeight)
                        render.fillStyle(KeyColor.colorFrom(j).color)
                        render.renderRect(boxWidth, (median - lowerQuartile) / gapY * gapHeight, IDiagramRender.DiagramStyle.fill)
                        render.fillStyle(BLACK_COLOR)
                        render.renderRect(boxWidth, (median - lowerQuartile) / gapY * gapHeight, IDiagramRender.DiagramStyle.stroke)
                        render.renderGroupEnd()

                        render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i, "data-x": xLabelList[i], "data-y": lowerQuartile, "data-label": "${xLabelList[i]}: ${lowerQuartile}", style: "pointer-events: bounding-box;"])
                        // line from lowerQuartile to lowerExtreme
                        render.translateTo(xWidth + boxWidth / 2, height - DIAGRAM_MARGIN_BOTTOM - (lowerExtreme - startLabelY) / gapY * gapHeight)
                        render.renderLine(0.0, -(lowerQuartile - lowerExtreme) / gapY * gapHeight)
                        // lowerQuartile line
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (lowerQuartile - startLabelY) / gapY * gapHeight)
                        render.renderLine(boxWidth, 0.0)
                        render.renderGroupEnd()

                        render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i, "data-x": xLabelList[i], "data-y": lowerExtreme, "data-label": "${xLabelList[i]}: ${lowerExtreme}"])
                        // lowerExtreme line
                        render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (lowerExtreme - startLabelY) / gapY * gapHeight)
                        render.renderLine(boxWidth, 0.0)
                        render.renderGroupEnd()
                    }
                } else {
                    render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "gap-index": i])
                    render.renderGroupEnd()
                }
                xWidth += boxWidth + boxMargin
            }
        }
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        buildTransformAreaStart("whiskers", MAX_BOX_WIDTH)
        drawVerticalBackgroundAndDataWhiskersBox()
        buildTransformAreaEnd()
    }
}