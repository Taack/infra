package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class BarDiagramScene extends RectBackgroundDiagramScene {
    final private BigDecimal MIN_BAR_WIDTH = 5.0
    final private BigDecimal MAX_BAR_WIDTH = 200.0

    final private boolean isStacked
    final private Map<String, List<BigDecimal>> yDataListPerKey

    BarDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, boolean isStacked) {
        super(render, dataPerKey, false)
        this.isStacked = isStacked

        Map<String, List<BigDecimal>> yDataListPerKey = [:]
        Set<String> keys = dataPerKey.keySet()
        for (int i = 0; i < keys.size(); i++) {
            String key = keys[i]
            yDataListPerKey.put(key, dataPerKey[key].values() as List<BigDecimal>)
        }
        this.yDataListPerKey = yDataListPerKey
    }

    void drawHorizontalBackground() {
        if (isStacked) {
            Set<BigDecimal> values = []
            Set<String> keys = yDataListPerKey.keySet()
            for (int i = 0; i < xLabelList.size(); i++) {
                BigDecimal value = 0.0
                for (int j = 0; j < keys.size(); j++) {
                    value += yDataListPerKey[keys[j]][i]
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

    void drawVerticalBackgroundAndDataBar() {
        Set<String> keys = yDataListPerKey.keySet()
        int showGapEveryX = 1
        BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / xLabelList.size()

        // bar width should be bigger than a min value (In the case of "smaller", we will combine several gaps to get enough space and only draw bars of the first gap. It means "showGapEveryX".)
        int barNumber = isStacked ? 1 : keys.size()
        BigDecimal singleBarWidth = barNumber > 1 ? (gapWidth * 0.8) * 0.8 / barNumber : gapWidth * 0.8
        if (singleBarWidth < MIN_BAR_WIDTH) {
            BigDecimal minGapWidth = barNumber > 1 ? MIN_BAR_WIDTH * barNumber / 0.8 / 0.8 : MIN_BAR_WIDTH / 0.8
            showGapEveryX = Math.ceil((minGapWidth / gapWidth).toDouble()).toInteger()
            gapWidth = gapWidth * showGapEveryX
        }

        super.drawVerticalBackground(true, showGapEveryX)

        // calculate true value of bar width
        BigDecimal gapHorizontalPadding = gapWidth * 0.2 / 2
        BigDecimal barWidth = barNumber > 1 ? (gapWidth * 0.8) * 0.8 / barNumber : gapWidth * 0.8
        BigDecimal barMargin = barNumber > 1 ? (gapWidth * 0.8) * 0.2 / (barNumber - 1) : 0.0
        if (barWidth > MAX_BAR_WIDTH) {
            barWidth = MAX_BAR_WIDTH
            gapHorizontalPadding = (gapWidth - barWidth * barNumber - barMargin * (barNumber - 1)) / 2
        }

        // data bar
        for (int i = 0; i < xLabelList.size() / showGapEveryX; i++) {
            BigDecimal barX = DIAGRAM_MARGIN_LEFT + gapWidth * i + gapHorizontalPadding
            BigDecimal barY = height - DIAGRAM_MARGIN_BOTTOM
            if (isStacked) {
                render.renderGroup(["element-type": ElementType.DATA_GROUP, "start-y": barY])
            } else {
                render.renderGroup(["element-type": ElementType.DATA_GROUP, "start-x": barX - gapHorizontalPadding, "gap-width": gapWidth, "max-shape-width": MAX_BAR_WIDTH])
            }
            for (int j = 0; j < keys.size(); j++) {
                BigDecimal yData = yDataListPerKey[keys[j]][i * showGapEveryX]
                BigDecimal barHeight = (yData - startLabelY) / gapY * gapHeight
                render.renderGroup(["element-type": ElementType.DATA, dataset: keys[j], "data-label": "${xLabelList[i]}: ${yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"}"])
                if (yData > startLabelY) {
                    // rect
                    render.translateTo(barX, barY - barHeight)
                    KeyColor rectColor = KeyColor.colorFrom(j)
                    render.fillStyle(rectColor.color)
                    render.renderRect(barWidth, barHeight, IDiagramRender.DiagramStyle.fill)
//                    // label
//                    String yDataLabel = yData.toDouble() % 1 == 0 ? "${yData.toInteger()}" : "$yData"
//                    render.translateTo(barX + (barWidth - render.measureText(yDataLabel)) / 2, isStacked ? barY - barHeight / 2 - fontSize / 2 : barY - barHeight - fontSize - 2.0)
//                    render.renderLabel(yDataLabel)
                }
                render.renderGroupEnd()

                if (isStacked) {
                    barY -= barHeight
                } else {
                    barX += barWidth + barMargin
                }
            }
            render.renderGroupEnd()
        }
    }

    void draw() {
        if (xLabelList.isEmpty() || yDataListPerKey.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        buildScrollStart()
        drawVerticalBackgroundAndDataBar()
        buildScrollEnd()
    }
}