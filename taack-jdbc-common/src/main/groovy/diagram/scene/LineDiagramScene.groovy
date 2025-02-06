package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        super(render, dataPerKey)
        this.dataPointRadius = 2.5
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        buildScrollStart()
        drawVerticalBackground(false)
        drawDataPoint(true)
        buildScrollEnd()
    }
}