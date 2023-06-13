package taack.ui.base

import groovy.transform.CompileStatic
import taack.ui.base.diagram.DiagramTypeSpec
import taack.ui.base.diagram.IUiDiagramVisitor

@CompileStatic
final class UiDiagramSpecifier {
    Closure closure

    UiDiagramSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) Closure closure) {
        this.closure = closure
        this
    }

    void visitDiagram(final IUiDiagramVisitor diagramVisitor) {
        if (diagramVisitor && closure) {
            diagramVisitor.visitDiagram()
            closure.delegate = new DiagramTypeSpec(diagramVisitor)
            closure.call()
            diagramVisitor.visitDiagramEnd()
        }
    }
}