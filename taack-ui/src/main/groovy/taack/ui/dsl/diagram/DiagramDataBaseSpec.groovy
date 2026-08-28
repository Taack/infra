package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramDataBaseSpec extends DiagramTypeSpec {

    DiagramDataBaseSpec(IUiDiagramVisitor diagramVisitor) {
        super(diagramVisitor, true)
    }

    void labels(Number... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(String... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(Date... dates) {
        diagramVisitor.visitLabels(dates)
    }

    void option(DiagramOption option) {
        diagramVisitor.visitDiagramOption(option)
    }
}
