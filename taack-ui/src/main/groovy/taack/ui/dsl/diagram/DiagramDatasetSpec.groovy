package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramDatasetSpec {
    final IUiDiagramVisitor diagramVisitor

    DiagramDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void dataset(final String key, final List<BigDecimal> yDataList) {
        diagramVisitor.dataset(key, yDataList)
    }

    void dataset(final String key, final BigDecimal pieData) {
        diagramVisitor.dataset(key, [pieData])
    }

    void dataset(final String key, final Map<Object, BigDecimal> dataMap) {
        diagramVisitor.dataset(key, dataMap)
    }
}
