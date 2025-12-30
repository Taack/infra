package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, DiagramOption diagramOption) {
        super(render, dataPerKey, diagramOption, [])
        this.dataPointRadius /= 4
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend()
        drawHorizontalBackground()
        buildTransformAreaStart('line')
        drawVerticalBackground()
        drawDataPoint(true)
        buildTransformAreaEnd()
    }
}