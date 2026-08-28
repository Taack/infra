package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramWhiskersDatasetSpec extends DiagramDataBaseSpec {

    DiagramWhiskersDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        super(diagramVisitor)
    }

    void dataset(final String key, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersBoxDataSpec) Closure closure) {
        closure.delegate = new DiagramWhiskersBoxDataSpec(key, diagramVisitor)
        closure.call()
    }
}
