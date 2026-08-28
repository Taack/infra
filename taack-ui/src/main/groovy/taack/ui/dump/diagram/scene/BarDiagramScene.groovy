package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class BarDiagramScene extends RectBackgroundDiagramScene {
    private BigDecimal MIN_BAR_WIDTH = 5.0
    private BigDecimal MAX_BAR_WIDTH = 200.0

    final private boolean isStacked
    private BigDecimal gapWidth
    private int showGapEveryX = 1

    BarDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption, boolean isStacked) {
        super(render, dataPerKey, diagramOption)
        this.isXLabelInsideGap = true
        this.isStacked = isStacked

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            MIN_BAR_WIDTH *= rate
            MAX_BAR_WIDTH *= rate
        }
    }

    String objectToString(Object o) {
        return o instanceof Date ? diagramOption.xLabelDateFormat.format(o) : o.toString()
    }

    void initData() {
        dataPerKey = dataPerKey.collectEntries { Map.Entry<String, Map<Object, BigDecimal>> keyDataMap -> // format xLabels from Date to String
            Map<Object, BigDecimal> formattedDataMap = [:]
            keyDataMap.value.each { Map.Entry<Object, BigDecimal> dataEntry ->
                String formattedLabel = objectToString(dataEntry.key)
                if (formattedDataMap.containsKey(formattedLabel) && dataEntry.key instanceof Date) { // sum the value of dates which are in same group
                    formattedDataMap[formattedLabel] += dataEntry.value
                } else {
                    formattedDataMap.put(formattedLabel, dataEntry.value)
                }
            }
            return [(keyDataMap.key): formattedDataMap]
        }
    }

    void initGapWidth() {
        gapWidth = (render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight) / xLabelList.size()
        if (!alwaysShowFullInfo || diagramOption.resolution == DiagramOption.DiagramResolution.FIT_CONTENT) {
            // bar width should be bigger than a min value (In the case of 'smaller', we will combine several gaps to get enough space and only draw bars of the first gap. It means 'showGapEveryX'.)
            int barNumber = isStacked ? 1 : dataPerKey.findAll { !it.value.isEmpty() }.keySet().size()
            BigDecimal singleBarWidth = barNumber > 1 ? (gapWidth * 0.8) * 0.8 / barNumber : gapWidth * 0.8
            if (singleBarWidth < MIN_BAR_WIDTH) {
                BigDecimal minGapWidth = barNumber > 1 ? MIN_BAR_WIDTH * barNumber / 0.8 / 0.8 : MIN_BAR_WIDTH / 0.8
                if (diagramOption.resolution == DiagramOption.DiagramResolution.FIT_CONTENT) {
                    gapWidth = minGapWidth
                    render.setDiagramWidth(gapWidth * xLabelList.size() + diagramMarginLeft + diagramMarginRight)
                } else {
                    showGapEveryX = Math.ceil((minGapWidth / gapWidth).toDouble()).toInteger()
                    gapWidth = gapWidth * showGapEveryX
                }
            }
        }
    }

    void drawHorizontalBackground() {
        if (isStacked) {
            Set<BigDecimal> values = []
            Set<String> keys = dataPerKey.keySet()
            xLabelList.forEach { x ->
                String xLabel = objectToString(x)
                BigDecimal value = 0.0
                for (int j = 0; j < keys.size(); j++) {
                    value += dataPerKey[keys[j]].get(xLabel) ?: 0.0
                }
                values.add(value)
            }
            values = values.sort() as Set<BigDecimal>
            BigDecimal minY = values.first() >= 0 ? 0.0 : Math.floor(values.first().toDouble()).toBigDecimal()
            BigDecimal maxY = values.last()
            super.drawHorizontalBackground(minY, maxY)
        } else {
            super.drawHorizontalBackground()
        }
    }

    void drawDataBar() {
        // calculate true value of bar width
        BigDecimal gapHorizontalPadding = gapWidth * 0.2 / 2
        int barNumber = isStacked ? 1 : dataPerKey.findAll { !it.value.isEmpty() }.keySet().size()
        BigDecimal barWidth = barNumber > 1 ? (gapWidth * 0.8) * 0.8 / barNumber : gapWidth * 0.8
        BigDecimal barMargin = barNumber > 1 ? (gapWidth * 0.8) * 0.2 / (barNumber - 1) : 0.0
        if (barWidth > MAX_BAR_WIDTH) {
            barWidth = MAX_BAR_WIDTH
            gapHorizontalPadding = (gapWidth - barWidth * barNumber - barMargin * (barNumber - 1)) / 2
        }

        // data bar
        Set<String> keys = dataPerKey.keySet()
        for (int i = 0; i < (xLabelList.size() / showGapEveryX).toInteger(); i++) {
            BigDecimal barX = diagramMarginLeft + gapWidth * i + gapHorizontalPadding
            BigDecimal barY = render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM
            for (int j = 0; j < keys.size(); j++) {
                Map<String, BigDecimal> data = dataPerKey[keys[j]] as Map<String, BigDecimal>
                if (!data.isEmpty()) {
                    String xLabel = objectToString(xLabelList[i * showGapEveryX])
                    BigDecimal yData = data.get(xLabel) ?: 0.0
                    String yDataLabel = numberToString(yData)
                    BigDecimal barHeight = (yData - startLabelY) / gapY * gapHeight
                    Color keyColor = getKeyColor(j)
                    render.renderGroup(['element-type': ElementType.TOOLTIP,
                                        'key-label': keys[j],
                                        'key-color': KeyColor.colorToString(keyColor),
                                        'key-description': "${xLabel}: ${yDataLabel}",
                                        'diagram-action-url': diagramOption?.clickActionUrl ?: '',
                                        'data-x': xLabel,
                                        'data-y': yDataLabel])
                    render.renderGroup(['element-type': ElementType.DATA,
                                        dataset: keys[j],
                                        'gap-index': i])
                    if (yData > startLabelY) {
                        // rect
                        render.translateTo(barX, barY - barHeight)
                        render.fillStyle(keyColor)
                        render.renderRect(barWidth, barHeight, IDiagramRender.DiagramStyle.fill)
                    }
                    render.renderGroupEnd()
                    render.renderGroupEnd()

                    // label
                    if (yData > startLabelY && diagramOption?.showDataCount) {
                        render.translateTo(barX + (barWidth - render.measureText(yDataLabel)) / 2, isStacked ? barY - barHeight / 2 - fontSize / 2 : barY - barHeight - fontSize - 2.0)
                        render.renderLabel(yDataLabel)
                    }

                    if (isStacked) {
                        barY -= barHeight
                    } else {
                        barX += barWidth + barMargin
                    }
                }
            }
        }
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        if (!buildXLabelList()) {
            return
        }
        super.draw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
        initData()
        initGapWidth()
        drawLegend()
        drawHorizontalBackground()
        buildClipSectionStart()
        drawVerticalBackground(showGapEveryX, gapWidth)
        buildTransformAreaStart(isStacked ? 'stackedBar' : 'bar', MAX_BAR_WIDTH)
        drawDataBar()
        render.renderGroupEnd()
        render.renderGroupEnd()
    }
}