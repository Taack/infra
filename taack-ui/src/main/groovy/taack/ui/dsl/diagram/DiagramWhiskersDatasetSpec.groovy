package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramWhiskersDatasetSpec extends DiagramActionSpec {

    DiagramWhiskersDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void labels(String... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void dataset(final String key, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersBoxDataSpec) Closure closure) {
        closure.delegate = new DiagramWhiskersBoxDataSpec(key, diagramVisitor)
        closure.call()
    }
}
