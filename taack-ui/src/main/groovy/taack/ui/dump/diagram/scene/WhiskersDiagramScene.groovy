package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class WhiskersDiagramScene extends RectBackgroundDiagramScene {
    private BigDecimal MIN_BOX_WIDTH = 5.0
    private BigDecimal MAX_BOX_WIDTH = 40.0
    private BigDecimal BOX_LINE_WIDTH = 2.0

    private List<Object> xDataList
    final private Map<String, List<List<BigDecimal>>> yDataListPerKey

    WhiskersDiagramScene(IDiagramRender render, Object[] xDataList, Map<String, List<List<BigDecimal>>> yDataListPerKey, DiagramOption diagramOption) {
        super(render, yDataListPerKey.collectEntries { [(it.key): xDataList.collectEntries { xData -> [(xData): 0.0] }] } as Map<String, Map<Object, BigDecimal>>, diagramOption)
        this.isXLabelInsideGap = true
        this.xDataList = xDataList.toList()
        this.yDataListPerKey = yDataListPerKey

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            MIN_BOX_WIDTH *= rate
            MAX_BOX_WIDTH *= rate
            BOX_LINE_WIDTH *= rate
        }
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

    String objectToString(Object o) {
        return o instanceof Date ? xLabelDateFormat.format(o) : o.toString()
    }

    void drawVerticalBackgroundAndDataWhiskersBox() {
        Set<String> keys = yDataListPerKey.keySet()
        int showGapEveryX = 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / xLabelList.size()
        if (!alwaysShowFullInfo) {
            // box width should be bigger than a min value (In the case of 'smaller', we will combine several gaps to get enough space and only draw boxes of the first gap. It means 'showGapEveryX'.)
            int boxNumber = keys.size()
            BigDecimal singleBoxWidth = boxNumber > 1 ? (gapWidth * 0.8) * 0.8 / boxNumber : gapWidth * 0.8
            if (singleBoxWidth < MIN_BOX_WIDTH) {
                BigDecimal minGapWidth = boxNumber > 1 ? MIN_BOX_WIDTH * boxNumber / 0.8 / 0.8 : MIN_BOX_WIDTH / 0.8
                showGapEveryX = Math.ceil((minGapWidth / gapWidth).toDouble()).toInteger()
                gapWidth = gapWidth * showGapEveryX
            }
        }
        super.drawVerticalBackground(showGapEveryX)

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
        xDataList = xDataList.collect { objectToString(it) } as List<Object>
        for (int i = 0; i < (xLabelList.size() / showGapEveryX).toInteger(); i++) {
            String xLabel = objectToString(xLabelList[i * showGapEveryX])
            int index = xDataList.lastIndexOf(xLabel)
            if (index >= 0) {
                BigDecimal xWidth = DIAGRAM_MARGIN_LEFT + gapWidth * i + gapHorizontalPadding
                for (int j = 0; j < keys.size(); j++) {
                    List<List<BigDecimal>> yDataList = yDataListPerKey[keys[j]]
                    List<BigDecimal> boxData = index < yDataList.size() ? yDataList[index].sort() : []
                    int size = boxData.size()
                    if (size > 0) {
                        BigDecimal lowerExtreme = boxData.first()
                        BigDecimal lowerQuartile = findMedian(boxData, 0, (size / 2).toInteger())
                        BigDecimal median = findMedian(boxData, 0, size)
                        BigDecimal upperQuartile = findMedian(boxData, (size / 2).toInteger() + size % 2, size)
                        BigDecimal upperExtreme = boxData.last()
                        Color keyColor = getKeyColor(j)
                        if (lowerExtreme > startLabelY) {
                            String yDataLabel = numberToString(upperExtreme)
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i, 'data-x': xLabel, 'data-y': yDataLabel, 'data-label': "${xLabel}: ${yDataLabel}", 'key-color': KeyColor.colorToString(keyColor), style: 'pointer-events: bounding-box;'])
                            // line from upperExtreme to upperQuartile
                            render.translateTo(xWidth + boxWidth / 2, height - DIAGRAM_MARGIN_BOTTOM - (upperExtreme - startLabelY) / gapY * gapHeight)
                            render.fillStyle(BLACK_COLOR)
                            render.renderLine(0.0, (upperExtreme - upperQuartile) / gapY * gapHeight)
                            // upperExtreme line
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (upperExtreme - startLabelY) / gapY * gapHeight)
                            render.renderLine(boxWidth, 0.0)
                            render.renderGroupEnd()

                            yDataLabel = numberToString(upperQuartile)
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i, 'data-x': xLabel, 'data-y': yDataLabel, 'data-label': "${xLabel}: ${yDataLabel}", 'key-color': KeyColor.colorToString(keyColor)])
                            // rect from upperQuartile to median
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (upperQuartile - startLabelY) / gapY * gapHeight)
                            render.fillStyle(keyColor)
                            render.renderRect(boxWidth, (upperQuartile - median) / gapY * gapHeight, IDiagramRender.DiagramStyle.fill)
                            render.fillStyle(BLACK_COLOR)
                            render.renderRect(boxWidth, (upperQuartile - median) / gapY * gapHeight, IDiagramRender.DiagramStyle.stroke)
                            render.renderGroupEnd()

                            yDataLabel = numberToString(median)
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i, 'data-x': xLabel, 'data-y': yDataLabel, 'data-label': "${xLabel}: ${yDataLabel}", 'key-color': KeyColor.colorToString(keyColor)])
                            // rect from median to lowerQuartile
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (median - startLabelY) / gapY * gapHeight)
                            render.fillStyle(keyColor)
                            render.renderRect(boxWidth, (median - lowerQuartile) / gapY * gapHeight, IDiagramRender.DiagramStyle.fill)
                            render.fillStyle(BLACK_COLOR)
                            render.renderRect(boxWidth, (median - lowerQuartile) / gapY * gapHeight, IDiagramRender.DiagramStyle.stroke)
                            render.renderGroupEnd()

                            yDataLabel = numberToString(lowerQuartile)
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i, 'data-x': xLabel, 'data-y': yDataLabel, 'data-label': "${xLabel}: ${yDataLabel}", 'key-color': KeyColor.colorToString(keyColor), style: 'pointer-events: bounding-box;'])
                            // line from lowerQuartile to lowerExtreme
                            render.translateTo(xWidth + boxWidth / 2, height - DIAGRAM_MARGIN_BOTTOM - (lowerExtreme - startLabelY) / gapY * gapHeight)
                            render.renderLine(0.0, -(lowerQuartile - lowerExtreme) / gapY * gapHeight)
                            // lowerQuartile line
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (lowerQuartile - startLabelY) / gapY * gapHeight)
                            render.renderLine(boxWidth, 0.0)
                            render.renderGroupEnd()

                            yDataLabel = numberToString(lowerExtreme)
                            render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i, 'data-x': xLabel, 'data-y': yDataLabel, 'data-label': "${xLabel}: ${yDataLabel}", 'key-color': KeyColor.colorToString(keyColor)])
                            // lowerExtreme line
                            render.translateTo(xWidth, height - DIAGRAM_MARGIN_BOTTOM - (lowerExtreme - startLabelY) / gapY * gapHeight)
                            render.renderLine(boxWidth, 0.0)
                            render.renderGroupEnd()
                        }
                    } else {
                        render.renderGroup(['element-type': ElementType.DATA, dataset: keys[j], 'gap-index': i])
                        render.renderGroupEnd()
                    }
                    xWidth += boxWidth + boxMargin
                }
            }
        }
    }

    void draw(boolean alwaysShowFullInfo = false) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend()
        drawHorizontalBackground()
        buildTransformAreaStart('whiskers', MAX_BOX_WIDTH)
        drawVerticalBackgroundAndDataWhiskersBox()
        buildTransformAreaEnd()
    }
}