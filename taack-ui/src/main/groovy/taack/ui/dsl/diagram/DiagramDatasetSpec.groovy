package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramDatasetSpec {
    final IUiDiagramVisitor diagramVisitor

    DiagramDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void dataset(final String key, final List<BigDecimal> data) {
        diagramVisitor.dataset(key, data)
    }

    void dataset(final String key, final BigDecimal data) {
        diagramVisitor.dataset(key, [data])
    }
}
