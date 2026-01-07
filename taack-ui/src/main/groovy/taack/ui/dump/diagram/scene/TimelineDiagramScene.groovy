package taack.ui.dump.diagram.scene

import grails.util.Triple
import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

@CompileStatic
class TimelineDiagramScene extends RectBackgroundDiagramScene {
    private BigDecimal MIN_TIMELINE_HEIGHT = 10.0
    private BigDecimal MAX_TIMELINE_HEIGHT = 20.0

    final private Map<String, List<Triple<Date, Date, String>>> timelineDataPerKey
    private BigDecimal timelineHeight
    private BigDecimal diagramMarginLeft = DIAGRAM_MARGIN_LEFT

    TimelineDiagramScene(IDiagramRender render, Map<String, List<Triple<Date, Date, String>>> timelineDataPerKey, DiagramOption diagramOption) {
        super(render, timelineDataPerKey.collectEntries { [(it.key): [it.value*.aValue + it.value*.bValue].flatten().grep().collectEntries { date -> [(date): 0.0] }] } as Map<String, Map<Object, BigDecimal>>, diagramOption)
        this.timelineDataPerKey = timelineDataPerKey.findAll { it.key != null }
        timelineDataPerKey.keySet().each { String keyLabel ->
            BigDecimal keyLabelLength = render.measureSmallText(keyLabel)
            if (diagramMarginLeft < keyLabelLength + AXIS_LABEL_MARGIN) {
                diagramMarginLeft = keyLabelLength + AXIS_LABEL_MARGIN
            }
        }

        BigDecimal rate = diagramOption?.resolution?.fontSizePercentage
        if (rate && rate != 1) {
            MIN_TIMELINE_HEIGHT *= rate
            MAX_TIMELINE_HEIGHT *= rate
        }
    }

    void initGapAndTimelineHeight() { // The timeline's height should normally be 61% of gap height, but limited by min and max
        gapHeight = (height - diagramMarginTop - DIAGRAM_MARGIN_BOTTOM) / timelineDataPerKey.size()
        if (alwaysShowFullInfo && gapHeight * 0.61 < MIN_TIMELINE_HEIGHT) { // Limited by min only when diagram is dynamic (Allowing scroll to have full view)
            timelineHeight = MIN_TIMELINE_HEIGHT
            gapHeight = timelineHeight / 0.61 // In this case, the last background horizontal line will exceed the diagram top edge
        } else if (gapHeight * 0.61 > MAX_TIMELINE_HEIGHT) {
            timelineHeight = MAX_TIMELINE_HEIGHT
        } else {
            timelineHeight = gapHeight * 0.61
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
        Set<String> keys = timelineDataPerKey.keySet()
        for (int i = 0; i <= keys.size(); i++) {
            // background horizontal line
            render.translateTo(diagramMarginLeft - BACKGROUND_LINE_EXCEED_DIAGRAM, diagramMarginTop + gapHeight * i)
            render.fillStyle(GREY_COLOR)
            render.renderLine(width - (diagramMarginLeft - BACKGROUND_LINE_EXCEED_DIAGRAM) - DIAGRAM_MARGIN_RIGHT, 0.0)

            // key label
            if (i < keys.size()) {
                String key = keys[i]
                render.translateTo(diagramMarginLeft - AXIS_LABEL_MARGIN - render.measureSmallText(key), diagramMarginTop + gapHeight * (i + 0.5) - fontSize * render.SMALL_LABEL_RATE / 2)
                render.renderSmallLabel(key)
            }
        }
        render.renderGroupEnd()
        render.renderGroupEnd()
    }

    void drawVerticalBackground() {
        String id = 'clipSection' + ThreadLocalRandom.current().nextInt(1, 1_000_000).toString()
        render.translateTo(0.0, 0.0)
        render.renderClipSection(id, [diagramMarginLeft - 1, diagramMarginTop,
                                      width - DIAGRAM_MARGIN_RIGHT + 1, diagramMarginTop,
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
        BigDecimal xLabelTotalLength = render.measureText(xLabelList.collect { xLabelDateFormat.format(it as Date) }.join(''))
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
            String xLabel = xLabelDateFormat.format(xLabelList[i] as Date)
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
        render.renderGroupEnd()

        render.renderGroupEnd()
        render.renderGroupEnd()
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
        Set<String> keys = timelineDataPerKey.keySet()
        for (int i = 0; i < keys.size(); i++) {
            // data timeline periods
            String key = keys[i]
            timelineDataPerKey[key].eachWithIndex { Triple<Date, Date, String> info, int index ->
                String periodTitle = info.cValue ?: ''
                Integer period = ((info.bValue.getTime() - info.aValue.getTime()) / (1000 * 60 * 60 * 24)).toInteger()
                String periodLabel = xLabelDateFormat.format(info.aValue) + ' -> ' + xLabelDateFormat.format(info.bValue)
                Color keyColor = getKeyColor(index)
                render.renderGroup(['element-type': ElementType.DATA,
                                    dataset: key,
                                    'dataset-suffix': periodTitle,
                                    'data-x': periodTitle,
                                    'data-y': periodLabel,
                                    'data-label': periodLabel + ' : ' + period.toString(),
                                    'key-color': KeyColor.colorToString(keyColor)])
                BigDecimal x = diagramMarginLeft + (objectToNumber(info.aValue) - minX) / (maxX - minX) * totalWidth
                BigDecimal y = diagramMarginTop + gapHeight * (i + 0.5)
                BigDecimal width = (objectToNumber(info.bValue) - objectToNumber(info.aValue)) / (maxX - minX) * totalWidth

                // period rect
                render.translateTo(x, y - timelineHeight / 2)
                render.fillStyle(keyColor)
                render.renderRect(width, timelineHeight, IDiagramRender.DiagramStyle.fill)

                render.renderGroupEnd()

                // period label
                if (diagramOption?.showDataCount) {
                    render.translateTo(x + (width - render.measureText(period.toString())) / 2, y - fontSize / 2)
                    render.renderLabel(period.toString())
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
        drawTitle()
        initGapAndTimelineHeight()
        drawHorizontalBackground()
        drawVerticalBackground()
        drawDataTimeline()
    }
}