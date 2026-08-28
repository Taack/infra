package taack.ui.dump

import grails.util.Pair
import grails.util.Triple
import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.DiagramOption
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
    Pair<DiagramType, Map> diagramType
    private Object[] xDataList
    private Map<String, Map<Object, BigDecimal>> dataPerKey = [:] // [key1: [xData1: yData1, xData2: yData2,...], key2: [...], ...]
    private Map<String, List<List<BigDecimal>>> whiskersYDataListPerKey = [:] // [key1: [yBoxData1, yBoxData2, ...], key2: [...], ...]; yBoxData = [data1, data2, ...]
    private Map<Triple<String, String, String>, List<Triple<Date, Date, String>>> timelineDataPerKey = [:] // [Triple(key, keyDescription, keyImageHref): [Triple(startDate, endDate, title), Triple(startDate2, endDate2, title2), Pair...], Triple() : [...], ...]
    private DiagramOption diagramOption = new DiagramOption()
    private boolean isComboDiagram = false
    private List<RawHtmlDiagramDump> comboDiagramList = []

    @Override
    void setDiagramBase(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    RawHtmlDiagramDump visitDiagram(DiagramType diagramType, Map params, boolean isComboDiagram) {
        if (!isComboDiagram) {
            this.diagramType = new Pair(diagramType, params)
            this.out.reset()
            this.xDataList = []
            this.dataPerKey = [:]
            this.whiskersYDataListPerKey = [:]
            this.timelineDataPerKey = [:]
            this.diagramOption = new DiagramOption()
            return this
        } else {
            RawHtmlDiagramDump comboDiagram = new RawHtmlDiagramDump(out)
            comboDiagram.isComboDiagram = true
            comboDiagram.diagramBase = diagramBase
            comboDiagram.diagramType = new Pair(diagramType, params)
            comboDiagram.xDataList = xDataList
            comboDiagram.diagramOption = diagramOption
            comboDiagramList.add(comboDiagram)
            return comboDiagram
        }
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
    void visitLabels(Date... dates) {
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
        SimpleDateFormat sdf = new SimpleDateFormat(diagramOption.xLabelDateFormat.dateFormat)
        Map<Object, BigDecimal> dataMap = dates.toList().groupBy { sdf.format(it) }.collectEntries { [(sdf.parse(it.key)): it.value.size()] }
        dataPerKey.put(key, dataMap)
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
    void timelinePeriodData(String key, String keyDescription, String keyImageHref, Date startDate, Date endDate, String title) {
        if (startDate && endDate) {
            Triple<Date, Date, String> info = startDate.before(endDate) ? new Triple(startDate, endDate, title) : new Triple(endDate, startDate, title)
            Triple<String, String, String> k = timelineDataPerKey.keySet().find { it.aValue == key }
            if (k) {
                timelineDataPerKey[k].add(info)
            } else {
                timelineDataPerKey.put(new Triple(key, keyDescription, keyImageHref), [info])
            }
        }
    }

    IDiagramRender initDiagramRender() {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            if (!diagramOption.resolution) {
                diagramOption.setResolution(DiagramOption.DiagramResolution.DEFAULT_2K)
            }
            return new PngDiagramRender(diagramOption.resolution)
        } else {
            if (!diagramOption.resolution) {
                diagramOption.setResolution(DiagramOption.DiagramResolution.DEFAULT_540P)
            }
            return new SvgDiagramRender(diagramOption.resolution, diagramBase == UiDiagramSpecifier.DiagramBase.SVG)
        }
    }

    DiagramScene initDiagramScene() {
        IDiagramRender render = initDiagramRender()
        switch (diagramType?.aValue) {
            case DiagramType.BAR:
                return new BarDiagramScene(render, dataPerKey, diagramOption, diagramType.bValue.getOrDefault('isStacked', true) as boolean)
            case DiagramType.SCATTER:
                return new ScatterDiagramScene(render, dataPerKey, diagramOption, diagramType.bValue.getOrDefault('pointImageHref', []) as List<String>)
            case DiagramType.LINE:
                return new LineDiagramScene(render, dataPerKey, diagramOption)
            case DiagramType.AREA:
                return new AreaDiagramScene(render, dataPerKey, diagramOption)
            case DiagramType.PIE:
                return new PieDiagramScene(render, dataPerKey, diagramOption, diagramType.bValue.getOrDefault('hasSlice', false) as boolean)
            case DiagramType.WHISKERS:
                return new WhiskersDiagramScene(render, xDataList, whiskersYDataListPerKey, diagramOption)
            case DiagramType.TIMELINE:
                return new TimelineDiagramScene(render, timelineDataPerKey, diagramOption)
            default:
                return null
        }
    }

    List<RawHtmlDiagramDump> getFullComboDiagramList(RawHtmlDiagramDump d) {
        List<RawHtmlDiagramDump> fullComboDiagramList = []
        if (!d.comboDiagramList.isEmpty()) {
            d.comboDiagramList.each { combo ->
                fullComboDiagramList.add(combo)
                fullComboDiagramList.addAll(getFullComboDiagramList(combo))
            }
        }
        return fullComboDiagramList
    }

    @Override
    void visitDiagramEnd() {
        if (!isComboDiagram) {
            List<RawHtmlDiagramDump> fullComboDiagramList = getFullComboDiagramList(this)
            if (!fullComboDiagramList.isEmpty()) {
                Set<String> mixedKeys = ([this] + fullComboDiagramList).collect { d ->
                    if (d.diagramType?.aValue == DiagramType.WHISKERS)
                        d.whiskersYDataListPerKey.keySet()
                    else if (d.diagramType?.aValue == DiagramType.TIMELINE)
                        d.timelineDataPerKey.keySet().collect { it.aValue }
                    else
                        d.dataPerKey.keySet()
                }.flatten() as Set<String>
                ([this] + fullComboDiagramList).each { d ->
                    if (d.diagramType?.aValue == DiagramType.WHISKERS)
                        d.whiskersYDataListPerKey = mixedKeys.collectEntries { String key -> [key, d.whiskersYDataListPerKey.getOrDefault(key, [])] }
                    else if (d.diagramType?.aValue == DiagramType.TIMELINE)
                        d.timelineDataPerKey = mixedKeys.collectEntries { String key ->
                            Triple<String, String, String> k = d.timelineDataPerKey.keySet().find { it.aValue == key } ?: new Triple(key, null, null)
                            [k, d.timelineDataPerKey.getOrDefault(k, [])]
                        }
                    else
                        d.dataPerKey = mixedKeys.collectEntries { String key -> [key, d.dataPerKey.getOrDefault(key, [:])] }
                }
            }
            DiagramScene scene = initDiagramScene()
            scene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, fullComboDiagramList.size(), fullComboDiagramList.size() + 1)
            fullComboDiagramList.eachWithIndex { comboDiagram, i ->
                ByteArrayOutputStream comboOutput = new ByteArrayOutputStream()
                DiagramScene comboDiagramScene = comboDiagram.initDiagramScene()
                comboDiagramScene.draw(diagramBase == UiDiagramSpecifier.DiagramBase.SVG, fullComboDiagramList.size(), i + 1)
                comboDiagramScene.render.output(comboOutput, false)
                scene.render.renderByteArray(comboOutput.toByteArray())
            }
            scene.render.output(out, true)
            if (mailAttachment != null) {
                String suffix = diagramBase == UiDiagramSpecifier.DiagramBase.PNG ? 'png' : 'svg'
                String fileName = ThreadLocalRandom.current().nextInt(0, 1_000_000).toString() + '-diagram.' + suffix
                mailAttachment.put(fileName, out.toByteArray())
                out.reset()
                out << """<img src="cid:${fileName}" style="display:block" width="720" height="360">"""
            }
            if (blockLog) {
                ByteArrayOutputStream clone = new ByteArrayOutputStream(4096)
                out.writeTo(clone)
                blockLog.topElement.addChildren(new HTMLOutput(clone))
            }
        }
    }

    @Override
    void visitDiagramOption(DiagramOption diagramOption) {
        this.diagramOption.title = diagramOption.title
        this.diagramOption.showDataCount = diagramOption.showDataCount
        this.diagramOption.hideLegend = diagramOption.hideLegend
        this.diagramOption.showTodayLine = diagramOption.showTodayLine
        this.diagramOption.keyColors = diagramOption.keyColors
        this.diagramOption.resolution = diagramOption.resolution
        this.diagramOption.clickActionUrl = diagramOption.clickActionUrl
        this.diagramOption.maxDataNumberToShowByDefault = diagramOption.maxDataNumberToShowByDefault
        this.diagramOption.xLabelDateFormat = diagramOption.xLabelDateFormat
    }

    @Override
    void visitCustom(String html) {
        out << html
        if (blockLog) {
            ByteArrayOutputStream clone = new ByteArrayOutputStream(4096)
            out.writeTo(clone)
            blockLog.topElement.addChildren(new HTMLOutput(clone))
        }
    }
}
