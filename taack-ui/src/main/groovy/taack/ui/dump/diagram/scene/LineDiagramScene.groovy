package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption) {
        super(render, dataPerKey, diagramOption, [])
        this.dataPointRadius /= 4
        this.dataPointClickableRadius /= 2
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        if (!buildXLabelList()) {
            return
        }
        super.rootDraw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
        this.isXLabelInsideGap = !(xLabelList.every { it instanceof Number } || xLabelList.every { it instanceof Date })
        drawLegend()
        drawHorizontalBackground()
        buildClipSectionStart()
        drawVerticalBackground()
        buildTransformAreaStart('line')
        drawDataPoint(true)
        render.renderGroupEnd()
        render.renderGroupEnd()
    }
}