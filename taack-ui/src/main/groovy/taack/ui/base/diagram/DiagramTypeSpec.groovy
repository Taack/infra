package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTypeSpec {
    final IUiDiagramVisitor diagramVisitor
    final DiagramDatasetSpec diagramDatasetSpec

    DiagramTypeSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
        this.diagramDatasetSpec = new DiagramDatasetSpec(diagramVisitor)
    }

    void bar(List<String> xLabels, String xTitle = "", String yTitle = "", boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitBarDiagram(xLabels)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagramEnd(xTitle, yTitle, isStacked)
    }

    void line(List<String> xLabels, String xTitle = "", String yTitle = "",
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitLineDiagram(xLabels)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagramEnd(xTitle, yTitle)
    }

    void pie(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitPieDiagram()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagramEnd()
    }
}
