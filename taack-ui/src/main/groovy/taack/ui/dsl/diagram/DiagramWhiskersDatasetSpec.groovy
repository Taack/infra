package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramWhiskersDatasetSpec {
    IUiDiagramVisitor diagramVisitor

    DiagramWhiskersDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
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

    void dataset(final String key, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersBoxDataSpec) Closure closure) {
        closure.delegate = new DiagramWhiskersBoxDataSpec(key, diagramVisitor)
        closure.call()
    }

    void option(DiagramOption option) {
        diagramVisitor.visitDiagramOption(option)
    }
}
