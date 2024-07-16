package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.DiagramTypeSpec
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender
import taack.ui.dump.diagram.scene.AreaDiagramScene
import taack.ui.dump.diagram.scene.BarDiagramScene
import taack.ui.dump.diagram.scene.LineDiagramScene
import taack.ui.dump.diagram.scene.PieDiagramScene
import taack.ui.dump.pdf.SvgDiagramRenderPdf

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out

    RawHtmlDiagramDump(final ByteArrayOutputStream out) {
        this.out = out
    }

    UiDiagramSpecifier.DiagramBase diagramBase
    private IDiagramRender render
    private Set<Object> xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramPreparation(Set<Object> xDataList, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio radio) {
        this.dataPerKey = [:]
        this.xDataList = xDataList
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            BigDecimal width = widthInPx ?: 960.0
            this.render = new SvgDiagramRender(width, width * radio.radio, widthInPx == null)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            BigDecimal width = widthInPx ?: 720.0
            this.render = new SvgDiagramRenderPdf(width, width * radio.radio)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            BigDecimal width = widthInPx ?: 720.0
            this.render = new PngDiagramRender(width, width * radio.radio)
        }
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

    @Override
    void visitBarDiagram(boolean isStacked) {
        BarDiagramScene scene = new BarDiagramScene(render, dataPerKey, isStacked)
        scene.draw()
    }

    @Override
    void visitLineDiagram() {
        LineDiagramScene scene = new LineDiagramScene(render, dataPerKey)
        scene.draw()
    }

    @Override
    void visitAreaDiagram(boolean isStacked) {
//        AreaDiagramScene scene = new AreaDiagramScene(render, xDataList, yDataPerKey, isStacked)
//        scene.draw()
    }

    @Override
    void visitPieDiagram(boolean hasSlice) {
        PieDiagramScene scene = new PieDiagramScene(render, dataPerKey, hasSlice)
        scene.draw()
    }

    @Override
    void visitDiagramEnd() {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            out << (this.render as SvgDiagramRender).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            out << (this.render as SvgDiagramRenderPdf).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            (this.render as PngDiagramRender).writeImage(out)
        }
    }
}
