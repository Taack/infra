package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey, boolean alwaysShowFullInfo = false) {
        super(render, dataPerKey, [], alwaysShowFullInfo)
        this.dataPointRadius = 2.5
    }

    void draw() {
        if (xLabelList.isEmpty()) {
            return
        }
        drawLegend()
        drawHorizontalBackground()
        buildTransformAreaStart("line")
        drawVerticalBackground(false)
        drawDataPoint(true)
        buildTransformAreaEnd()
    }
}