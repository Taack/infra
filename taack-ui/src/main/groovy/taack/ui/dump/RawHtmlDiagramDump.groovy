package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.DiagramXLabelDateFormat
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender
import taack.ui.dump.diagram.scene.*
import taack.ui.dump.html.block.HTMLOutput

import java.text.SimpleDateFormat
import java.util.concurrent.ThreadLocalRandom

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    private final BlockLog blockLog
    final Map<String, byte[]> mailAttachment

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final BlockLog blockLog = null, final Map<String, byte[]> mailAttachment = null) {
        this.out = out
        this.blockLog = blockLog
        this.mailAttachment = mailAttachment
    }

    UiDiagramSpecifier.DiagramBase diagramBase
    private IDiagramRender render
    private Object[] xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]
    private Map<String, List<List<BigDecimal>>> whiskersYDataListPerKey // [key1: [yBoxData1, yBoxData2, ...], key2: [...], ...]; yBoxData = [data1, data2, ...]
    private String diagramActionUrl
    private DiagramXLabelDateFormat xLabelDateFormat = DiagramXLabelDateFormat.DAY

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramDataInitialization() {
        this.out.reset()
        this.xDataList = []
        this.dataPerKey = [:]
        this.whiskersYDataListPerKey = [:]
        this.diagramActionUrl = null
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
    void visitLabels(DiagramXLabelDateFormat dateFormat, Date... dates) {
        this.xLabelDateFormat = dateFormat
        this.xDataList = dates
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
            } else if (!xDataList.every { it instanceof Number || it instanceof Date }) {
                for (int i = dataMap.size(); i < xDataList.size(); i++) {
                    dataMap.put(xDataList[i], 0.0)
                }
            }
            dataPerKey.put(key, dataMap)
        }
    }

    @Override
    void dataset(String key, Date... dates) {
        SimpleDateFormat sdf = new SimpleDateFormat((xLabelDateFormat ?: DiagramXLabelDateFormat.DAY).dateFormat)
        Map<Object, BigDecimal> dataMap = dates.toList().groupBy { sdf.format(it) }.collectEntries { [(sdf.parse(it.key)): it.value.size()] }
        dataPerKey.put(key, dataMap)
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
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, diagramActionUrl)
    }

    @Override
    void visitScatterDiagram(String... pointImageHref) {
        createDiagramRender(0.5)
        ScatterDiagramScene scene = new ScatterDiagramScene(render, dataPerKey, pointImageHref.toList())
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, diagramActionUrl)
    }

    @Override
    void visitLineDiagram() {
        createDiagramRender(0.5)
        LineDiagramScene scene = new LineDiagramScene(render, dataPerKey)
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, diagramActionUrl)
    }

    @Override
    void visitAreaDiagram() {
        createDiagramRender(0.5)
        AreaDiagramScene scene = new AreaDiagramScene(render, dataPerKey)
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(false, diagramActionUrl)
    }

    @Override
    void visitPieDiagram(boolean hasSlice) {
        createDiagramRender(1.0)
        PieDiagramScene scene = new PieDiagramScene(render, dataPerKey, hasSlice)
        scene.draw(diagramActionUrl)
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
            if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
            scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, diagramActionUrl)
        }
    }

    @Override
    void visitDiagramEnd() {
        if (mailAttachment == null) {
            if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
                (this.render as PngDiagramRender).writeImage(out)
            } else {
                out << (this.render as SvgDiagramRender).getRendered()
            }
        } else {
            ByteArrayOutputStream fileStream = new ByteArrayOutputStream()
            String suffix
            if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
                (this.render as PngDiagramRender).writeImage(fileStream)
                suffix = 'png'
            } else {
                fileStream << (this.render as SvgDiagramRender).getRendered()
                suffix = 'svg'
            }
            String fileName = ThreadLocalRandom.current().nextInt(0, 1_000_000).toString() + '-diagram.' + suffix
            mailAttachment.put(fileName, fileStream.toByteArray())
            out << """<img src="cid:${fileName}" style="display:block" width="720" height="360">"""
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

    @Override
    void visitDiagramAction(String controller, String action, Long id, Map<String, ?> params) {
        diagramActionUrl = (new Parameter(Parameter.RenderingTarget.WEB)).urlMapped(controller, action, id, params)
    }
}
