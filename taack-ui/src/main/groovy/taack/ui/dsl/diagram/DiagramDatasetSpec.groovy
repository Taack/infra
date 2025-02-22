package taack.ui.dsl.diagram

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

    void dataset(final String key, final BigDecimal... yDataList) {
        diagramVisitor.dataset(key, yDataList)
    }

    void dataset(final String key, final BigDecimal pieData) {
        diagramVisitor.dataset(key, pieData)
    }

    void dataset(final String key, final Map<Object, BigDecimal> dataMap) {
        diagramVisitor.dataset(key, dataMap)
    }
}
