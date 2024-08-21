package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramWhiskersBoxDataSpec {
    final String key
    final IUiDiagramVisitor diagramVisitor

    DiagramWhiskersBoxDataSpec(final String key, final IUiDiagramVisitor diagramVisitor) {
        this.key = key
        this.diagramVisitor = diagramVisitor
    }

    void boxData(BigDecimal... boxDataList) {
        diagramVisitor.whiskersBoxData(key, boxDataList)
    }
}
