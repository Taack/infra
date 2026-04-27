package taack.ui.dump.diagram.scene

import grails.util.Triple
import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dsl.diagram.DiagramXLabelDateFormat
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

@CompileStatic
class TimelineDiagramScene extends RectBackgroundDiagramScene {
    private BigDecimal MIN_TIMELINE_HEIGHT = 10.0
    private BigDecimal MAX_TIMELINE_HEIGHT = 20.0
    private BigDecimal TIMELINE_HEIGHT_RATE = 0.395
    private BigDecimal MAX_DIAGRAM_MARGIN_LEFT = DIAGRAM_MARGIN_LEFT * 2
    private BigDecimal SCROLL_BAR_WIDTH = 10.0

    final private Map<Triple<String, String, String>, List<Triple<Date, Date, String>>> timelineDataPerKey
    private BigDecimal timelineHeight
    private BigDecimal diagramMarginLeft = DIAGRAM_MARGIN_LEFT
    private static boolean multiPeriods = true

    static Map<String, Map<Object, BigDecimal>> translateTimelineData(Map<Triple<String, String, String>, List<Triple<Date, Date, String>>> timelineDataPerKey) {
        // make a tmp data so that diagram has correct X axe and correct legends
        if (timelineDataPerKey.size() > 0) {
            List<String> legends = []
            timelineDataPerKey.collect { it.value.collect { it.cValue } }.each { List<String> it ->
                if (it.size() > legends.size()) {
                    legends = it
                }
            }
            multiPeriods = legends.find { !it.isBlank() }
            Map<String, Map<Object, BigDecimal>> result = (multiPeriods ? legends.collectEntries { [(it): [:]] } : timelineDataPerKey.collectEntries { [(it.key.aValue): [:]] }) as Map<String, Map<Object, BigDecimal>>
            List<Date> xDataList = timelineDataPerKey.collect { [it.value*.aValue + it.value*.bValue] }.flatten().grep() as List<Date>
            result.put(null, xDataList.collectEntries { date -> [(date): 0.0] })
            return result
        } else {
            return [:]
        }
    }

    TimelineDiagramScene(IDiagramRender render, Map<Triple<String, String, String>, List<Triple<Date, Date, String>>> timelineDataPerKey, DiagramOption diagramOption) {
        super(render, translateTimelineData(timelineDataPerKey), diagramOption)
        this.timelineDataPerKey = timelineDataPerKey.findAll { it.key != null }
        timelineDataPerKey.keySet()*.aValue.each { String keyLabel ->
            BigDecimal keyLabelLength = render.measureText(keyLabel)
            if (diagramMarginLeft < keyLabelLength + AXIS_LABEL_MARGIN) {
                diagramMarginLeft = keyLabelLength + AXIS_LABEL_MARGIN
            }
        }
        if (diagramMarginLeft > MAX_DIAGRAM_MARGIN_LEFT) {
            diagramMarginLeft = MAX_DIAGRAM_MARGIN_LEFT
        }

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            MIN_TIMELINE_HEIGHT *= rate
            MAX_TIMELINE_HEIGHT *= rate
        }
    }

    void initGapAndTimelineHeight() { // The timeline's height should normally be x% of gap height, but limited by min and max
        gapHeight = (height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM) / timelineDataPerKey.size()
        if (alwaysShowFullInfo && gapHeight * TIMELINE_HEIGHT_RATE < MIN_TIMELINE_HEIGHT) { // Limited by min only when diagram is dynamic (Allowing scroll to have full view)
            timelineHeight = MIN_TIMELINE_HEIGHT
            gapHeight = timelineHeight / TIMELINE_HEIGHT_RATE // In this case, the last background horizontal line will exceed the diagram top edge
        } else if (gapHeight * TIMELINE_HEIGHT_RATE > MAX_TIMELINE_HEIGHT) {
            timelineHeight = MAX_TIMELINE_HEIGHT
        } else {
            timelineHeight = gapHeight * TIMELINE_HEIGHT_RATE
        }
    }

    void drawHorizontalBackground() {
        String id = 'clipSection' + ThreadLocalRandom.current().nextInt(0, 1_000_000).toString()
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [0.0, diagramMarginTop - 1,
                                      width, diagramMarginTop - 1,
                                      width, height - DIAGRAM_MARGIN_BOTTOM + BACKGROUND_LINE_EXCEED_DIAGRAM,
                                      0.0, height - DIAGRAM_MARGIN_BOTTOM + BACKGROUND_LINE_EXCEED_DIAGRAM])
        render.renderGroup(['clip-path': "url(#${id})"])
        render.renderGroup(['element-type': ElementType.HORIZONTAL_BACKGROUND])
        Set<Triple<String, String, String>> keys = timelineDataPerKey.keySet()
        for (int i = 0; i <= keys.size(); i++) {
            // background horizontal line
            render.translateTo(diagramMarginLeft - BACKGROUND_LINE_EXCEED_DIAGRAM, diagramMarginTop + gapHeight * i)
            render.fillStyle(GREY_COLOR)
            render.renderLine(width - (diagramMarginLeft - BACKGROUND_LINE_EXCEED_DIAGRAM) - DIAGRAM_MARGIN_RIGHT, 0.0)

            // key label
            if (i < keys.size()) {
                String key = keys[i].aValue
                render.renderGroup(['element-type': ElementType.TOOLTIP,
                                    'key-label': key,
                                    'key-color': multiPeriods ? '' : KeyColor.colorToString(getKeyColor(i)),
                                    'key-description': keys[i].bValue ?: '',
                                    'key-image-href': keys[i].cValue ?: '',
                                    'x-scrolled': false])
                if (render.measureText(key) <= diagramMarginLeft - AXIS_LABEL_MARGIN) {
                    render.translateTo(diagramMarginLeft - AXIS_LABEL_MARGIN - render.measureText(key), diagramMarginTop + gapHeight * (i + 0.5) - fontSize / 2)
                    render.renderLabel(key)
                } else {
                    while (render.measureSmallText(key) > diagramMarginLeft - AXIS_LABEL_MARGIN) {
                        key = key.substring(0, key.size() - 6) + '...'
                    }
                    render.translateTo(diagramMarginLeft - AXIS_LABEL_MARGIN - render.measureSmallText(key), diagramMarginTop + gapHeight * (i + 0.5) - fontSize * render.SMALL_LABEL_RATE / 2)
                    render.renderSmallLabel(key)
                }
                render.renderGroupEnd()
            }
        }
        render.renderGroupEnd()
        render.renderGroupEnd()
    }

    void drawVerticalBackground() {
        String id = 'clipSection' + ThreadLocalRandom.current().nextInt(1, 1_000_000).toString()
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [diagramMarginLeft - 1, diagramMarginTop - DIAGRAM_MARGIN_TOP,
                                      width - DIAGRAM_MARGIN_RIGHT + 1, diagramMarginTop - DIAGRAM_MARGIN_TOP,
                                      width - DIAGRAM_MARGIN_RIGHT + 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                      width, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                      width, height,
                                      0.0, height,
                                      0.0, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN,
                                      diagramMarginLeft - 1, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN])
        render.renderGroup(['clip-path': "url(#${id})"])

        int displayedXLabelListNumber = xLabelList.size()
        BigDecimal diagramWidth = width - diagramMarginLeft - DIAGRAM_MARGIN_RIGHT
        BigDecimal gapWidth = diagramWidth / (displayedXLabelListNumber > 1 ? displayedXLabelListNumber - 1 : 1)
        BigDecimal xLabelTotalLength = render.measureText(xLabelList.collect { diagramOption.xLabelDateFormat.format(it as Date) }.join(''))
        int showLabelEveryX = Math.ceil((xLabelTotalLength / (diagramWidth * 0.8)).toDouble()).toInteger()
        render.renderGroup(['element-type': ElementType.VERTICAL_BACKGROUND, 'show-label-every-x': xLabelTotalLength / (diagramWidth * 0.8)])
        render.fillStyle(GREY_COLOR)
        BigDecimal minX = objectToNumber(xLabelList.first())
        BigDecimal maxX = objectToNumber(xLabelList.last())
        for (int i = 0; i < displayedXLabelListNumber; i++) {
            BigDecimal x = objectToNumber(xLabelList[i])
            BigDecimal coordX = diagramMarginLeft + (x - minX) / (maxX - minX) * diagramWidth

            // background vertical line
            render.translateTo(coordX, diagramMarginTop)
            if (i % showLabelEveryX == 0) {
                render.renderLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
            } else if (alwaysShowFullInfo) {
                render.renderHiddenLine(0.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM))
            }

            // x axis label
            String xLabel = diagramOption.xLabelDateFormat.format(xLabelList[i] as Date)
            BigDecimal labelLength = render.measureText(xLabel)
            if (gapWidth >= labelLength) {
                render.translateTo(coordX - labelLength / 2, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                if (i % showLabelEveryX == 0) {
                    render.renderLabel(xLabel)
                } else if (alwaysShowFullInfo) {
                    render.renderHiddenLabel(xLabel)
                }
            } else {
                render.translateTo(coordX - labelLength, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                if (i % showLabelEveryX == 0) {
                    render.renderRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, coordX, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                } else if (alwaysShowFullInfo) {
                    render.renderHiddenRotatedLabel(xLabel, LABEL_ROTATE_ANGLE_WHEN_MASSIVE, coordX, height - DIAGRAM_MARGIN_BOTTOM + AXIS_LABEL_MARGIN)
                }
            }
        }
        if (diagramOption.showTodayLine && xLabelList.every { it instanceof Date }) {
            // red vertical line for TODAY
            BigDecimal todayX = diagramMarginLeft + (objectToNumber(new Date()) - minX) / (maxX - minX) * diagramWidth
            render.translateTo(todayX, diagramMarginTop)
            render.fillStyle(Color.RED)
            render.renderRect(3.0, height - diagramMarginTop - (DIAGRAM_MARGIN_BOTTOM - BACKGROUND_LINE_EXCEED_DIAGRAM), IDiagramRender.DiagramStyle.fill)

            // date label for TODAY
            if (diagramOption.showDataCount) {
                String dateLabel = DiagramXLabelDateFormat.DAY.format(new Date())
                render.translateTo(todayX - render.measureText(dateLabel) / 2, diagramMarginTop - fontSize)
                render.renderLabel(dateLabel)
            }
        }

        render.renderGroupEnd()
        render.renderGroupEnd()
        render.renderGroupEnd()
    }

    void drawVerticalScrollBar() {
        BigDecimal diagramHeight = height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM
        if ((gapHeight * timelineDataPerKey.size() * 100).toInteger() / 100 > diagramHeight) {
            render.renderGroup(['element-type': ElementType.VERTICAL_SCROLL_BAR])
            render.translateTo(width - (DIAGRAM_MARGIN_RIGHT - SCROLL_BAR_WIDTH) / 2 - SCROLL_BAR_WIDTH, diagramMarginTop)
            render.fillStyle(GREY_COLOR)
            render.renderRect(SCROLL_BAR_WIDTH, diagramHeight * (diagramHeight / (gapHeight * timelineDataPerKey.size())), IDiagramRender.DiagramStyle.fill)
            render.renderGroupEnd()
        }
    }

    void drawDataTimeline() {
        String id = 'clipSection' + ThreadLocalRandom.current().nextInt(2, 1_000_000).toString()
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [diagramMarginLeft - 1, diagramMarginTop,
                                      width - DIAGRAM_MARGIN_RIGHT + 1, diagramMarginTop,
                                      width - DIAGRAM_MARGIN_RIGHT + 1, height - DIAGRAM_MARGIN_BOTTOM + BACKGROUND_LINE_EXCEED_DIAGRAM,
                                      diagramMarginLeft - 1, height - DIAGRAM_MARGIN_BOTTOM + BACKGROUND_LINE_EXCEED_DIAGRAM])
        render.renderGroup(['clip-path': "url(#${id})"])
        render.renderGroup(['element-type': ElementType.TRANSFORM_AREA,
                            'diagram-action-url': diagramOption?.clickActionUrl ?: '',
                            'shape-type': 'timeline',
                            'shape-max-width': 0.0,
                            'area-min-x': diagramMarginLeft,
                            'area-max-x': width - DIAGRAM_MARGIN_RIGHT,
                            'area-min-y': diagramMarginTop,
                            'area-max-y': height - DIAGRAM_MARGIN_BOTTOM])

        BigDecimal minX = objectToNumber(xLabelList.first())
        BigDecimal maxX = objectToNumber(xLabelList.last())
        BigDecimal totalWidth = width - diagramMarginLeft - DIAGRAM_MARGIN_RIGHT
        Set<Triple<String, String, String>> keys = timelineDataPerKey.keySet()
        DiagramXLabelDateFormat dateFormat = DiagramXLabelDateFormat.DAY
        for (int i = 0; i < keys.size(); i++) {
            // data timeline periods
            Triple<String, String, String> key = keys[i]
            BigDecimal y = diagramMarginTop + gapHeight * (i + 0.5) // center Y inside gap
            timelineDataPerKey[key].eachWithIndex { Triple<Date, Date, String> info, int index ->
                String periodTitle = info.cValue ?: ''
                Integer period = ((info.bValue.getTime() - info.aValue.getTime()) / (1000 * 60 * 60 * 24)).toInteger()
                String periodLabel = dateFormat.format(info.aValue) + ' -> ' + dateFormat.format(info.bValue)
                Color keyColor = getKeyColor(multiPeriods ? index : i)
                render.renderGroup(['element-type': ElementType.TOOLTIP,
                                    'key-label': multiPeriods ? "${key.aValue} : $periodTitle" : key.aValue,
                                    'key-color': KeyColor.colorToString(keyColor),
                                    'key-description': periodLabel + ' : ' + period.toString()])
                render.renderGroup(['element-type': ElementType.DATA,
                                    dataset: multiPeriods ? periodTitle : key.aValue,
                                    'data-x': periodLabel,
                                    'data-y': key.aValue])
                BigDecimal x = diagramMarginLeft + (objectToNumber(info.aValue) - minX) / (maxX - minX) * totalWidth
                BigDecimal width = (objectToNumber(info.bValue) - objectToNumber(info.aValue)) / (maxX - minX) * totalWidth

                // period rect
                render.translateTo(x, y - timelineHeight / 2)
                render.fillStyle(keyColor)
                render.renderRect(width, timelineHeight, IDiagramRender.DiagramStyle.fill)

                render.renderGroupEnd()
                render.renderGroupEnd()

                if (diagramOption?.showDataCount) {
                    // period label
                    render.translateTo(x + (width - render.measureText(period.toString())) / 2, y - fontSize / 2)
                    render.renderLabel(period.toString())

                    // startDate
                    if (index == 0) {
                        String dateLabel = dateFormat.format(info.aValue)
                        render.translateTo(x - AXIS_LABEL_MARGIN - render.measureText(dateLabel), y - fontSize / 2)
                        render.renderLabel(dateLabel)
                    }

                    // endDate
                    if (index == timelineDataPerKey[key].size() - 1) {
                        render.translateTo(x + width + AXIS_LABEL_MARGIN, y - fontSize / 2)
                        render.renderLabel(dateFormat.format(info.bValue))
                    }
                }
            }
        }

        render.renderGroupEnd()
        render.renderGroupEnd()
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend()
        initGapAndTimelineHeight()
        drawHorizontalBackground()
        drawVerticalBackground()
        drawVerticalScrollBar()
        drawDataTimeline()
    }
}