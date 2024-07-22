package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramTypeSpec
import taack.ui.dsl.diagram.IUiDiagramVisitor

@CompileStatic
final class UiDiagramSpecifier {
    Closure closure

    UiDiagramSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) Closure closure) {
        this.closure = closure
        this
    }

    enum DiagramBase {
        SVG,
        SVG_PDF,
        PNG
    }
    void visitDiagram(final IUiDiagramVisitor diagramVisitor, final DiagramBase diagramBase) {
        if (diagramVisitor && closure) {
            diagramVisitor.visitDiagram(diagramBase)
            closure.delegate = new DiagramTypeSpec(diagramVisitor)
            closure.call()
            diagramVisitor.visitDiagramEnd()
        }
    }
}