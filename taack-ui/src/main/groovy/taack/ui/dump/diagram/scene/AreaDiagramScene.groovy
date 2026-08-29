package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class AreaDiagramScene extends RectBackgroundDiagramScene {
    private Map<String, Object> stackedDataPerKey = [:]
    private BigDecimal minY
    private BigDecimal maxY

    AreaDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption) {
        super(render, dataPerKey, diagramOption)
    }

    void initData() {
        if (!isXLabelInsideGap) { // continuous
            // rebuild data to be stacked : Map<String, Map<BigDecimal, BigDecimal>> stackedDataPerKey
            Set<BigDecimal> totalXDataSet = dataPerKey.collect { it.value.keySet() }.flatten().unique().sort().collect { objectToNumber(it) } as Set<BigDecimal>
            List<BigDecimal> stackedYDataTmpList = [0.0] * totalXDataSet.size()
            minY = 0.0
            maxY = 0.0
            Set<String> keys = dataPerKey.keySet()
            for (int i = 0; i < keys.size(); i++) {
                Map<BigDecimal, BigDecimal> dataMap = dataPerKey[keys[i]].collectEntries { [(objectToNumber(it.key)): it.value] } as Map<BigDecimal, BigDecimal>
                Set<BigDecimal> xDataSet = dataMap.keySet().sort() as Set<BigDecimal>
                Map<BigDecimal, BigDecimal> stackedDataMap = [:]
                if (!xDataSet.isEmpty()) {
                    for (int j = 0; j < totalXDataSet.size(); j++) {
                        BigDecimal x = totalXDataSet[j]
                        BigDecimal stackedY = stackedYDataTmpList[j]
                        if (x >= xDataSet.first() && x <= xDataSet.last()) {
                            if (xDataSet.contains(x)) {
                                stackedY += dataMap[x]
                            } else {
                                int index = xDataSet.findIndexOf { it > x }
                                BigDecimal x1 = xDataSet[index - 1]
                                BigDecimal y1 = dataMap[x1]
                                BigDecimal x2 = xDataSet[index]
                                BigDecimal y2 = dataMap[x2]
                                stackedY += (y2 - y1) / (x2 - x1) * (x - x1) + y1
                            }
                            stackedDataMap.put(x, stackedY)

                            if (stackedY < minY) minY = stackedY
                            if (stackedY > maxY) maxY = stackedY
                        }
                        stackedYDataTmpList[j] = stackedY
                    }
                }
                stackedDataPerKey.put(keys[i], stackedDataMap)
            }
        } else { // discrete
            // rebuild data to be stacked : Map<String, List<BigDecimal>> stackedDataPerKey
            for (e in dataPerKey) {
                List<BigDecimal> yDataList = e.value.values() as List<BigDecimal>
                if (!yDataList.isEmpty()) {
                    if (xLabelList.size() - yDataList.size() > 0) {
                        yDataList.addAll([0.0] * (xLabelList.size() - yDataList.size()))
                    }
                    List<BigDecimal> previousYDataList = stackedDataPerKey.isEmpty() ? [0.0] * xLabelList.size() : stackedDataPerKey[stackedDataPerKey.keySet().last()] as List<BigDecimal>
                    stackedDataPerKey.put(e.key, [yDataList, previousYDataList].transpose().collect { (it as List<BigDecimal>)[0] + (it as List<BigDecimal>)[1] })
                }
            }

            // draw horizontal background
            Set<String> keys = stackedDataPerKey.keySet()
            minY = Math.min(Math.floor((stackedDataPerKey[keys.first()] as List<BigDecimal>).min().toDouble()), 0.0 as Double).toBigDecimal()
            maxY = (stackedDataPerKey[keys.last()] as List<BigDecimal>).max()
        }
    }

    void drawDataArea() {
        if (!isXLabelInsideGap) { // continuous
            // draw data area from top to lowest, and next one will cover the previous one
            BigDecimal minX = objectToNumber(xLabelList.first())
            BigDecimal maxX = objectToNumber(xLabelList.last())
            BigDecimal totalWidth = render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight
            Set<String> keys = dataPerKey.keySet()
            for (int i = keys.size() - 1; i >= 0; i--) {
                Map<BigDecimal, BigDecimal> dataMap = stackedDataPerKey[keys[i]] as Map<BigDecimal, BigDecimal>
                if (!dataMap.isEmpty()) {
                    Set<BigDecimal> xDataSet = dataMap.keySet()
                    Color keyColor = getKeyColor(i)
                    render.renderGroup(['element-type': ElementType.TOOLTIP,
                                        'key-label': keys[i],
                                        'key-color': KeyColor.colorToString(keyColor),
                                        'key-description': dataPerKey[keys[i]].collect { "${objectToNumber(it.key)}: ${it.value}" }.join(',<br />'),
                                        'diagram-action-url': diagramOption?.clickActionUrl ?: ''])
                    render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i]])
                    List<BigDecimal> coordsToDraw = [] // x1, y1, x2, y2, ...
                    for (int j = 0; j < xDataSet.size(); j++) {
                        BigDecimal xWidth = (xDataSet[j] - minX) / (maxX - minX) * totalWidth

                        coordsToDraw.add(diagramMarginLeft + xWidth)
                        coordsToDraw.add(render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM)

                        coordsToDraw.add(0, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - (dataMap[xDataSet[j]] - startLabelY) / gapY * gapHeight)
                        coordsToDraw.add(0, diagramMarginLeft + xWidth)
                    }

                    render.translateTo(0.0, 0.0)
                    render.fillStyle(keyColor)
                    render.renderPoly(coordsToDraw, IDiagramRender.DiagramStyle.fill)

                    render.renderGroupEnd()
                    render.renderGroupEnd()
                }
            }
        } else { // discrete
            // draw data area one by one
            BigDecimal gapWidth = (render.getDiagramWidth() - diagramMarginLeft - diagramMarginRight) / xLabelList.size()
            Set<String> keys = stackedDataPerKey.keySet()
            for (int i = 0; i < keys.size(); i++) {
                Color keyColor = getKeyColor(dataPerKey.keySet().toList().indexOf(keys[i]))
                render.renderGroup(['element-type': ElementType.TOOLTIP,
                                    'key-label': keys[i],
                                    'key-color': KeyColor.colorToString(keyColor),
                                    'key-description': dataPerKey[keys[i]].collect { "${it.key}: ${it.value}" }.join(',<br />'),
                                    'diagram-action-url': diagramOption?.clickActionUrl ?: ''])
                render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i]])

                List<BigDecimal> y1List = i > 0 ? stackedDataPerKey[keys[i - 1]] as List<BigDecimal> : [minY] * xLabelList.size()
                List<BigDecimal> y2List = stackedDataPerKey[keys[i]] as List<BigDecimal>

                List<BigDecimal> coordsToDraw = [] // x1, y1, x2, y2, ...
                for (int j = 0; j < xLabelList.size(); j++) {
                    BigDecimal xWidth = gapWidth * (j + 0.5)

                    coordsToDraw.add(diagramMarginLeft + xWidth)
                    coordsToDraw.add(render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - (y1List[j] - startLabelY) / gapY * gapHeight)

                    coordsToDraw.add(0, render.getDiagramHeight() - DIAGRAM_MARGIN_BOTTOM - (y2List[j] - startLabelY) / gapY * gapHeight)
                    coordsToDraw.add(0, diagramMarginLeft + xWidth)
                }

                render.translateTo(0.0, 0.0)
                render.fillStyle(keyColor)
                render.renderPoly(coordsToDraw, IDiagramRender.DiagramStyle.fill)

                render.renderGroupEnd()
                render.renderGroupEnd()
            }
        }
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        if (!buildXLabelList()) {
            return
        }
        super.draw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
        this.isXLabelInsideGap = !(xLabelList.every { it instanceof Number } || xLabelList.every { it instanceof Date })
        initData()
        drawLegend()
        drawHorizontalBackground(minY, maxY)
        buildClipSectionStart()
        drawVerticalBackground()
        buildTransformAreaStart('area')
        drawDataArea()
        render.renderGroupEnd()
        render.renderGroupEnd()
    }
}