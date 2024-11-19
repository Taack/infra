package taack.ui.dump

import diagram.IDiagramRender
import diagram.PngDiagramRender
import diagram.SvgDiagramRender
import diagram.scene.AreaDiagramScene
import diagram.scene.BarDiagramScene
import diagram.scene.LineDiagramScene
import diagram.scene.PieDiagramScene
import diagram.scene.ScatterDiagramScene
import diagram.scene.WhiskersDiagramScene
import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.block.HTMLOutput
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.layout.HTMLEmpty

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    private final BlockLog blockLog
    final BootstrapLayout layout

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final BlockLog blockLog = null) {
        this.out = out
        this.blockLog = blockLog
        layout = new BootstrapLayout(blockLog)
    }

    UiDiagramSpecifier.DiagramBase diagramBase
    private IDiagramRender render
    private Object[] xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]
    private Map<String, List<List<BigDecimal>>> whiskersYDataListPerKey // [key1: [yBoxData1, yBoxData2, ...], key2: [...], ...]; yBoxData = [data1, data2, ...]

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramDataInitialization() {
        this.out.reset()
        this.dataPerKey = [:]
        this.whiskersYDataListPerKey = [:]
    }

    @Override
    void visitLabels(Number... labels) {
        this.xDataList = labels
    }

    @Override
    void visitLabels(String... labels) {
        this.xDataList = labels
    }

    @Override
    void dataset(String key, BigDecimal... yDataList) {
        if (xDataList) {
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
            if (!xDataList) {
                xDataList = dataMap.keySet().toArray()
            } else if (!xDataList.every { it instanceof Number }) {
                for (int i = dataMap.size(); i < xDataList.size(); i++) {
                    dataMap.put(xDataList[i], 0.0)
                }
            }
            dataPerKey.put(key, dataMap)
        }
    }

    void createDiagramRender(BigDecimal heightWidthRadio) {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            BigDecimal width = 960.0
            this.render = new SvgDiagramRender(width, width * heightWidthRadio, true)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_PDF) {
            BigDecimal width = 720.0
            this.render = new SvgDiagramRender(width, width * heightWidthRadio, false)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            BigDecimal width = 720.0
            this.render = new PngDiagramRender(width, width * heightWidthRadio)
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
    void whiskersBoxData(String key, BigDecimal... boxData) {
        if (xDataList) {
            List<List<BigDecimal>> yDataList = whiskersYDataListPerKey.get(key) ?: []
            yDataList.add(boxData.toList())
            whiskersYDataListPerKey[key] = yDataList
        }
    }

    @Override
    void visitWhiskersDiagram() {
        if (xDataList) {
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
        if (blockLog) {
            ByteArrayOutputStream clone = new ByteArrayOutputStream()
            out.writeTo(clone)
            blockLog.topElement.addChildren(new HTMLOutput(clone))
        }
    }

    @Override
    void visitCustom(String html) {
        out << html
        if (blockLog) {
            ByteArrayOutputStream clone = new ByteArrayOutputStream()
            out.writeTo(clone)
            blockLog.topElement.addChildren(new HTMLOutput(clone))
        }
    }

    private List<String> currentTabNames = []
    private IHTMLElement oldParent = null
    private int tabOccurrence = 0

    @Override
    void visitDiagramTabs() {
        if (blockLog) {
            oldParent = blockLog.topElement
            oldParent.setTaackTag(TaackTag.TABS)
            blockLog.topElement = new HTMLEmpty()
        }
    }

    @Override
    void visitDiagramTab(final String i18n) {
        if (blockLog) {
            currentTabNames << i18n
            blockLog.topElement.setTaackTag(TaackTag.TAB)
            blockLog.topElement = layout.tab(blockLog.topElement, tabOccurrence++)
        }
    }

    @Override
    void visitDiagramTabEnd() {
        if (blockLog) {
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TAB)
        }
    }

    @Override
    void visitDiagramTabsEnd() {
        if (blockLog) {
            IHTMLElement tabsContent = blockLog.topElement
            blockLog.topElement = layout.tabs(oldParent, currentTabNames)
            blockLog.topElement.addChildren(tabsContent)
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABS)
        }
    }
}
