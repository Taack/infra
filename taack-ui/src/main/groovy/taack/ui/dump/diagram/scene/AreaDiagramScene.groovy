package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color

@CompileStatic
class AreaDiagramScene extends RectBackgroundDiagramScene {
    AreaDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption) {
        super(render, dataPerKey, diagramOption)
    }

    void drawHorizontalBackgroundAndDataArea() {
        Set<String> keys = dataPerKey.keySet()
        if (xLabelList.every { it instanceof Number } || xLabelList.every { it instanceof Date }) { // continuous
            // rebuild data to be stacked
            Map<String, Map<BigDecimal, BigDecimal>> stackedDataPerKey = [:]
            Set<BigDecimal> totalXDataSet = dataPerKey.collect { it.value.keySet() }.flatten().unique().sort().collect { objectToNumber(it) } as Set<BigDecimal>
            List<BigDecimal> stackedYDataTmpList = [0.0] * totalXDataSet.size()
            BigDecimal minY = 0.0
            BigDecimal maxY = 0.0
            for (int i = 0; i < keys.size(); i++) {
                Map<BigDecimal, BigDecimal> dataMap = dataPerKey[keys[i]].collectEntries { [(objectToNumber(it.key)): it.value] } as Map<BigDecimal, BigDecimal>
                Set<BigDecimal> xDataSet = dataMap.keySet().sort() as Set<BigDecimal>

                Map<BigDecimal, BigDecimal> stackedDataMap = [:]
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
                stackedDataPerKey.put(keys[i], stackedDataMap)
            }

            // draw horizontal background
            super.drawHorizontalBackground(minY, maxY)

            // draw data area from top to lowest, and next one will cover the previous one
            BigDecimal minX = objectToNumber(xLabelList.first())
            BigDecimal maxX = objectToNumber(xLabelList.last())
            BigDecimal totalWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
            for (int i = keys.size() - 1; i >= 0; i--) {
                Map<BigDecimal, BigDecimal> dataMap = stackedDataPerKey[keys[i]]
                Set<BigDecimal> xDataSet = dataMap.keySet()
                Color keyColor = getKeyColor(i)
                render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i], 'data-x': '', 'data-y': '', 'key-color': KeyColor.colorToString(keyColor)])
                List<BigDecimal> coordsToDraw = [] // x1, y1, x2, y2, ...
                for (int j = 0; j < xDataSet.size(); j++) {
                    BigDecimal xWidth = (xDataSet[j] - minX) / (maxX - minX) * totalWidth

                    coordsToDraw.add(DIAGRAM_MARGIN_LEFT + xWidth)
                    coordsToDraw.add(height - DIAGRAM_MARGIN_BOTTOM)

                    coordsToDraw.add(0, height - DIAGRAM_MARGIN_BOTTOM - (dataMap[xDataSet[j]] - startLabelY) / gapY * gapHeight)
                    coordsToDraw.add(0, DIAGRAM_MARGIN_LEFT + xWidth)
                }

                render.translateTo(0.0, 0.0)
                render.fillStyle(keyColor)
                render.renderPoly(coordsToDraw, IDiagramRender.DiagramStyle.fill)

                render.renderGroupEnd()
            }
        } else { // discrete
            // rebuild data to be stacked
            Map<String, List<BigDecimal>> stackedYDataListPerKey = [:]
            for (int i = 0; i < keys.size(); i++) {
                List<BigDecimal> yDataList = dataPerKey[keys[i]].values() as List<BigDecimal>
                if (xLabelList.size() - yDataList.size() > 0) yDataList.addAll([0.0] * (xLabelList.size() - yDataList.size()))

                List<BigDecimal> previousYDataList = i > 0 ? stackedYDataListPerKey[keys[i - 1]] : [0.0] * xLabelList.size()
                stackedYDataListPerKey.put(keys[i], [yDataList, previousYDataList].transpose().collect { (it as List<BigDecimal>)[0] + (it as List<BigDecimal>)[1] })
            }

            // draw horizontal background
            BigDecimal minY = Math.min(Math.floor(stackedYDataListPerKey[keys.first()].min().toDouble()), 0.0 as Double).toBigDecimal()
            BigDecimal maxY = stackedYDataListPerKey[keys.last()].max()
            super.drawHorizontalBackground(minY, maxY)

            // draw data area one by one
            BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / (xLabelList.size() - 1)
            for (int i = 0; i < keys.size(); i++) {
                Color keyColor = getKeyColor(i)
                render.renderGroup(['element-type': ElementType.DATA, dataset: keys[i], 'data-x': '', 'data-y': '', 'key-color': KeyColor.colorToString(keyColor)])

                List<BigDecimal> y1List = i > 0 ? stackedYDataListPerKey[keys[i - 1]] : [minY] * xLabelList.size()
                List<BigDecimal> y2List = stackedYDataListPerKey[keys[i]]

                List<BigDecimal> coordsToDraw = [] // x1, y1, x2, y2, ...
                for (int j = 0; j < xLabelList.size(); j++) {
                    BigDecimal xWidth = gapWidth * j

                    coordsToDraw.add(DIAGRAM_MARGIN_LEFT + xWidth)
                    coordsToDraw.add(height - DIAGRAM_MARGIN_BOTTOM - (y1List[j] - startLabelY) / gapY * gapHeight)

                    coordsToDraw.add(0, height - DIAGRAM_MARGIN_BOTTOM - (y2List[j] - startLabelY) / gapY * gapHeight)
                    coordsToDraw.add(0, DIAGRAM_MARGIN_LEFT + xWidth)
                }

                render.translateTo(0.0, 0.0)
                render.fillStyle(keyColor)
                render.renderPoly(coordsToDraw, IDiagramRender.DiagramStyle.fill)

                render.renderGroupEnd()
            }
        }
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend()
        render.renderGroup(['element-type': ElementType.TRANSFORM_AREA, 'diagram-action-url': diagramOption?.clickActionUrl ?: '', 'shape-type': 'area', 'shape-max-width': 0.0, 'area-min-x': DIAGRAM_MARGIN_LEFT, 'area-max-x': width - DIAGRAM_MARGIN_RIGHT, 'area-min-y': diagramMarginTop, 'area-max-y': height - DIAGRAM_MARGIN_BOTTOM])
        drawVerticalBackground()
        drawHorizontalBackgroundAndDataArea()
        render.renderGroupEnd()
    }
}