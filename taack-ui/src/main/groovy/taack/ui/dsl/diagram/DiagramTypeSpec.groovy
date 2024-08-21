package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTypeSpec {
    final IUiDiagramVisitor diagramVisitor
    final DiagramDatasetSpec diagramDatasetSpec

    DiagramTypeSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
        this.diagramDatasetSpec = new DiagramDatasetSpec(diagramVisitor)
    }

    void bar(boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagram(isStacked)
    }

    void scatter(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              String... pointImageHref) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitScatterDiagram(pointImageHref)
    }

    void line(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void area(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitAreaDiagram()
    }

    void pie(boolean hasSlice, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagram(hasSlice)
    }

    void whiskers(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization(null, null)
        closure.delegate = new DiagramWhiskersDatasetSpec(diagramVisitor)
        closure.call()
        diagramVisitor.visitWhiskersDiagram()
    }
}
