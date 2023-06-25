package taack.ui.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.diagram.render.IDiagramRender

@CompileStatic
class PieDiagramScene extends DiagramScene {

    PieDiagramScene(IDiagramRender render, Map<String, List<BigDecimal>> yDataPerKey) {
        this.width = render.getDiagramWidth()
        this.height = render.getDiagramHeight()
        this.render = render
        this.yDataPerKey = yDataPerKey
        this.legendFullColor = true
    }

    void drawDataPie() {
        BigDecimal total = yDataPerKey.values().collect { it.size() ? it.first() : 0 }.sum() as BigDecimal
        BigDecimal radius = (Math.min(((width - DIAGRAM_MARGIN_LEFT - DIAGRAM_MARGIN_RIGHT) / 2).toDouble(), (height - diagramMarginTop).toDouble()) / 2).toBigDecimal()
        BigDecimal centerX = width / 2
        BigDecimal centerY = diagramMarginTop + radius

        // sector
        BigDecimal angle1 = 0.0
        yDataPerKey.eachWithIndex { Map.Entry<String, List<BigDecimal>> it, int i ->
            BigDecimal value = it.value.size() ? it.value.first() : 0.0
            BigDecimal percent = value / total
            BigDecimal angle2 = angle1 + 360.0 * percent
            render.translateTo(centerX, centerY)
            render.fillStyle(LegendColor.colorFrom(i))
            render.renderSector(radius, angle1, angle2, IDiagramRender.DiagramStyle.fill)

            angle1 = angle2
        }

        // label
        angle1 = 0.0
        yDataPerKey.eachWithIndex { Map.Entry<String, List<BigDecimal>> it, int i ->
            BigDecimal value = it.value.size() ? it.value.first() : 0.0
            BigDecimal percent = value / total
            BigDecimal angle2 = angle1 + 360.0 * percent
            Double labelAngle = (((angle1 + angle2) / 2 - 90.0) * Math.PI / 180.0) as Double
            BigDecimal labelX = centerX + radius * Math.cos(labelAngle) * 3 / 4
            BigDecimal labelY = centerY + radius * Math.sin(labelAngle) * 3 / 4
            String valueLabel = "${it.key}: ${value.toDouble() % 1 == 0 ? "${value.toInteger()}" : "$value"}"
            render.translateTo(labelX - render.measureText(valueLabel) / 2, labelY - FONT_SIZE)
            render.renderLabel(valueLabel)
            String percentLabel = "(${(percent * 100).round(2)}%)"
            render.translateTo(labelX - render.measureText(percentLabel) / 2, labelY)
            render.renderLabel(percentLabel)

            angle1 = angle2
        }
    }

    void draw() {
        if (yDataPerKey.keySet().isEmpty()) {
            return
        }
        drawLegend()
        drawDataPie()
    }
}