package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dump.diagram.IDiagramRender

import java.awt.*
import java.util.List

@CompileStatic
class AreaDiagramScene extends ScatterDiagramScene {
    AreaDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        super(render, dataPerKey)
        this.dataPointRadius = 0.0
        this.legendFullColor = true
    }

    void drawHorizontalBackgroundAndDataArea() {
        Set<String> keys = dataPerKey.keySet()
        if (xLabelList.every { it instanceof Number }) { // continuous
            // rebuild data to be stacked
            Map<String, Map<Number, BigDecimal>> stackedDataPerKey = [:]
            Set<Number> totalXDataSet = dataPerKey.collect { it.value.keySet() }.flatten().unique().sort() as Set<Number>
            List<BigDecimal> stackedYDataTmpList = [0.0] * totalXDataSet.size()
            BigDecimal minY = 0.0
            BigDecimal maxY = 0.0
            for (int i = 0; i < keys.size(); i++) {
                Map<Number, BigDecimal> dataMap = dataPerKey[keys[i]] as Map<Number, BigDecimal>
                Set<Number> xDataSet = dataMap.keySet().sort() as Set<Number>

                Map<Number, BigDecimal> stackedDataMap = [:]
                for (int j = 0; j < totalXDataSet.size(); j++) {
                    Number x = totalXDataSet[j]
                    BigDecimal stackedY = stackedYDataTmpList[j]
                    if (x >= xDataSet.first() && x <= xDataSet.last()) {
                        if (xDataSet.contains(x)) {
                            stackedY += dataMap[x]
                        } else {
                            int index = xDataSet.findIndexOf { it > x }
                            Number x1 = xDataSet[index - 1]
                            BigDecimal y1 = dataMap[x1]
                            Number x2 = xDataSet[index]
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
            drawHorizontalBackground(minY, maxY)

            // draw data area from top to lowest, and next one will cover the previous one
            Integer minX = xLabelList.first() as Integer
            Integer maxX = xLabelList.last() as Integer
            BigDecimal totalWidth = width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT
            for (int i = keys.size() - 1; i >= 0; i--) {
                Map<Number, BigDecimal> dataMap = stackedDataPerKey[keys[i]]
                Set<Number> xDataSet = dataMap.keySet()

                List<BigDecimal> coordsToDraw = [] // x1, y1, x2, y2, ...
                for (int j = 0; j < xDataSet.size(); j++) {
                    BigDecimal xWidth = (xDataSet[j] - minX) / (maxX - minX) * totalWidth

                    coordsToDraw.add(DIAGRAM_MARGIN_LEFT + xWidth)
                    coordsToDraw.add(height - DIAGRAM_MARGIN_BOTTOM)

                    coordsToDraw.add(0, height - DIAGRAM_MARGIN_BOTTOM - (dataMap[xDataSet[j]] - startLabelY) / gapY * gapHeight)
                    coordsToDraw.add(0, DIAGRAM_MARGIN_LEFT + xWidth)
                }

                render.translateTo(0.0, 0.0)
//                Color legendColor = LegendColor.colorFrom(i)
//                render.fillStyle(new Color(legendColor.red, legendColor.green, legendColor.blue, 128))
                render.fillStyle(LegendColor.colorFrom(i))
                render.renderPoly(coordsToDraw)
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
            drawHorizontalBackground(minY, maxY)

            // draw data area one by one
            BigDecimal gapWidth = (width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / (xLabelList.size() - 1)
            for (int i = 0; i < keys.size(); i++) {
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
//                Color legendColor = LegendColor.colorFrom(i)
//                render.fillStyle(new Color(legendColor.red, legendColor.green, legendColor.blue, 128))
                render.fillStyle(LegendColor.colorFrom(i))
                render.renderPoly(coordsToDraw)
            }
        }
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawVerticalBackground()
        drawHorizontalBackgroundAndDataArea()
    }
}