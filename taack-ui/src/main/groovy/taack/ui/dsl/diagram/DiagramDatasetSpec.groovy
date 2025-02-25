package taack.ui.dsl.diagram

import diagram.scene.DiagramXLabelDateFormat
import groovy.transform.CompileStatic

@CompileStatic
class DiagramDatasetSpec extends DiagramActionSpec {

    DiagramDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void labels(Number... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(String... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(DiagramXLabelDateFormat dateFormat = DiagramXLabelDateFormat.DAY, Date... dates) {
        diagramVisitor.visitLabels(dateFormat, dates)
    }

    void dataset(final String key, final BigDecimal... yDataList) {
        diagramVisitor.dataset(key, yDataList)
    }

    void dataset(final String key, final BigDecimal yData) {
        diagramVisitor.dataset(key, yData)
    }

    void dataset(final String key, final Map<Object, BigDecimal> dataMap) {
        diagramVisitor.dataset(key, dataMap)
    }
}
