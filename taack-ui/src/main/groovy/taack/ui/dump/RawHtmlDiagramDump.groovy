package taack.ui.dump

import grails.util.Triple
import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.DiagramOption
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
    DiagramScene scene
    private IDiagramRender render
    private Object[] xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]
    private Map<String, List<List<BigDecimal>>> whiskersYDataListPerKey // [key1: [yBoxData1, yBoxData2, ...], key2: [...], ...]; yBoxData = [data1, data2, ...]
    private Map<String, List<Triple<Date, Date, String>>> timelineDataPerKey // [key1: [Triple(startDate, endDate, title), Triple(startDate2, endDate2, title2), Pair...], key2 : [...], ...]
    private DiagramOption diagramOption = new DiagramOption()
    private DiagramXLabelDateFormat xLabelDateFormat = DiagramXLabelDateFormat.DAY

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramDataInitialization() {
        this.out.reset()
        this.scene = null
        this.xDataList = []
        this.dataPerKey = [:]
        this.whiskersYDataListPerKey = [:]
        this.timelineDataPerKey = [:]
        this.diagramOption = new DiagramOption()
        this.xLabelDateFormat = DiagramXLabelDateFormat.DAY
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

    void initDiagramResolution(boolean squareResolution = false) {
        diagramOption ?= new DiagramOption()
        if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            if (!diagramOption.resolution) {
                diagramOption.setResolution(squareResolution ? DiagramOption.DiagramResolution.SQUARE_HIGH : DiagramOption.DiagramResolution.DEFAULT_2K)
            }
            render = new PngDiagramRender(diagramOption.resolution)
        } else {
            if (!diagramOption.resolution) {
                diagramOption.setResolution(squareResolution ? DiagramOption.DiagramResolution.SQUARE : DiagramOption.DiagramResolution.DEFAULT_540P)
            }
            render = new SvgDiagramRender(diagramOption.resolution, diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
        }
    }

    @Override
    void visitBarDiagram(boolean isStacked) {
        initDiagramResolution()
        scene = new BarDiagramScene(render, dataPerKey, diagramOption, isStacked)
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitScatterDiagram(String... pointImageHref) {
        initDiagramResolution()
        scene = new ScatterDiagramScene(render, dataPerKey, diagramOption, pointImageHref.toList())
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitLineDiagram() {
        initDiagramResolution()
        scene = new LineDiagramScene(render, dataPerKey, diagramOption)
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitAreaDiagram() {
        initDiagramResolution()
        scene = new AreaDiagramScene(render, dataPerKey, diagramOption)
        if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
        scene.draw(false)
    }

    @Override
    void visitPieDiagram(boolean hasSlice) {
        initDiagramResolution(true)
        scene = new PieDiagramScene(render, dataPerKey, diagramOption, hasSlice)
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
            initDiagramResolution()
            scene = new WhiskersDiagramScene(render, xDataList, whiskersYDataListPerKey, diagramOption)
            if (xLabelDateFormat) scene.setXLabelDateFormat(xLabelDateFormat)
            scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
        }
    }

    @Override
    void timelinePeriodData(String key, Date startDate, Date endDate, String title) {
        if (startDate && endDate) {
            Triple<Date, Date, String> info = startDate.before(endDate) ? new Triple(startDate, endDate, title) : new Triple(endDate, startDate, title)
            if (timelineDataPerKey.containsKey(key)) {
                timelineDataPerKey[key].add(info)
            } else {
                timelineDataPerKey.put(key, [info])
            }
        }
    }

    @Override
    void visitTimelineDiagram() {
        initDiagramResolution()
        if (xDataList) timelineDataPerKey.put(null, xDataList.collect { new Triple<Date, Date, String>(it as Date, null, null) })
        scene = new TimelineDiagramScene(render, timelineDataPerKey, diagramOption)
        scene.setXLabelDateFormat(DiagramXLabelDateFormat.DAY)
        scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
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
    void visitDiagramOption(DiagramOption diagramOption) {
        this.diagramOption = diagramOption
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
}
