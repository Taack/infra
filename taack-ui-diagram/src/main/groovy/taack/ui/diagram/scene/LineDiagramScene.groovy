package diagram.scene

import groovy.transform.CompileStatic
import diagram.IDiagramRender

@CompileStatic
class LineDiagramScene extends ScatterDiagramScene {
    LineDiagramScene(IDiagramRender render, Map<String, Map<Object, BigDecimal>> dataPerKey) {
        super(render, dataPerKey, [])
        this.dataPointRadius = 2.5
    }

    void draw(boolean alwaysShowFullInfo = false, String diagramActionUrl = null) {
        if (!buildXLabelList()) {
            return
        }
        this.alwaysShowFullInfo = alwaysShowFullInfo
        drawLegend()
        drawHorizontalBackground()
        buildTransformAreaStart("line", diagramActionUrl)
        drawVerticalBackground()
        drawDataPoint(true)
        buildTransformAreaEnd()
    }
}