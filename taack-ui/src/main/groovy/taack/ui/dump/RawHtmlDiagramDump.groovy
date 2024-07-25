package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender
import taack.ui.dump.diagram.scene.AreaDiagramScene
import taack.ui.dump.diagram.scene.BarDiagramScene
import taack.ui.dump.diagram.scene.LineDiagramScene
import taack.ui.dump.diagram.scene.PieDiagramScene
import taack.ui.dump.diagram.scene.ScatterDiagramScene
import taack.ui.dump.diagram.scene.WhiskersDiagramScene

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out

    RawHtmlDiagramDump(final ByteArrayOutputStream out) {
        this.out = out
    }

    UiDiagramSpecifier.DiagramBase diagramBase
    private BigDecimal diagramWidth
    private BigDecimal diagramHeight
    private IDiagramRender render
    private Set<Object> xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]
    private Map<String, List<List<BigDecimal>>> whiskersYDataListPerKey // [key1: [yBoxData1, yBoxData2, ...], key2: [...], ...]; yBoxData = [data1, data2, ...]

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramDataInitialization(Set<Object> xDataList, BigDecimal widthInPx, BigDecimal heightInPx) {
        this.xDataList = xDataList
        this.diagramWidth = widthInPx
        this.diagramHeight = heightInPx
        this.dataPerKey = [:]
        this.whiskersYDataListPerKey = [:]
    }

    @Override
    void dataset(String key, List<BigDecimal> yDataList) {
        if (!xDataList.isEmpty()) {
            Map<Object, BigDecimal> dataMap = [:]
            for (i in 0..< xDataList.size()) {
                if (i < yDataList.size()) {
                    dataMap.put(xDataList[i], yDataList[i])
                } else {
                    dataMap.put(xDataList[i], 0.0)
                }
            }
            dataPerKey.put(key, dataMap)
        }
    }

    @Override
    void dataset(String key, Map<Object, BigDecimal> dataMap) {
        if (!dataMap.isEmpty()) {
            if (xDataList.isEmpty()) {
                xDataList = dataMap.keySet()
            } else if (!xDataList.every { it instanceof Number }) {
                for (int i = dataMap.size(); i < xDataList.size(); i++) {
                    dataMap.put(xDataList[i], 0.0)
                }
            }
            dataPerKey.put(key, dataMap)
        }
    }

    void createDiagramRender(BigDecimal defaultHeightWidthRadio) {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            BigDecimal width = diagramWidth ?: 960.0
            this.render = new SvgDiagramRender(width, diagramHeight ?: (width * defaultHeightWidthRadio), diagramWidth == null)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_PDF) {
            BigDecimal width = diagramWidth ?: 720.0
            this.render = new SvgDiagramRender(width, diagramHeight ?: (width * defaultHeightWidthRadio), false)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            BigDecimal width = diagramWidth ?: 720.0
            this.render = new PngDiagramRender(width, diagramHeight ?: (width * defaultHeightWidthRadio))
        }
    }

    @Override
    void visitBarDiagram(boolean isStacked) {
        createDiagramRender(0.5)
        BarDiagramScene scene = new BarDiagramScene(render, dataPerKey, isStacked)
        scene.draw()
    }

    @Override
    void visitScatterDiagram(String... pointImageHref) {
        createDiagramRender(0.5)
        ScatterDiagramScene scene = new ScatterDiagramScene(render, dataPerKey, pointImageHref)
        scene.draw()
    }

    @Override
    void visitLineDiagram() {
        createDiagramRender(0.5)
        LineDiagramScene scene = new LineDiagramScene(render, dataPerKey)
        scene.draw()
    }

    @Override
    void visitAreaDiagram() {
        createDiagramRender(0.5)
        AreaDiagramScene scene = new AreaDiagramScene(render, dataPerKey)
        scene.draw()
    }

    @Override
    void visitPieDiagram(boolean hasSlice) {
        createDiagramRender(1.0)
        PieDiagramScene scene = new PieDiagramScene(render, dataPerKey, hasSlice)
        scene.draw()
    }

    @Override
    void whiskersBoxData(String key, List<BigDecimal> boxData) {
        if (!xDataList.isEmpty()) {
            List<List<BigDecimal>> yDataList = whiskersYDataListPerKey.get(key) ?: []
            yDataList.add(boxData)
            whiskersYDataListPerKey[key] = yDataList
        }
    }

    @Override
    void visitWhiskersDiagram() {
        if (!xDataList.isEmpty()) {
            createDiagramRender(0.5)
            WhiskersDiagramScene scene = new WhiskersDiagramScene(render, xDataList, whiskersYDataListPerKey)
            scene.draw()
        }
    }

    @Override
    void visitDiagramEnd() {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            (this.render as PngDiagramRender).writeImage(out)
        } else {
            out << (this.render as SvgDiagramRender).getRendered()
        }
    }
}
