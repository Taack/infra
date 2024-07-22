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

    void bar(Set<Object> xDataList, boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
             BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization(xDataList, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagram(isStacked)
    }

    void scatter(Set<Object> xDataList, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null, String... pointImageHref) {
        diagramVisitor.visitDiagramDataInitialization(xDataList, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitScatterDiagram(pointImageHref)
    }

    void scatter(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null, String... pointImageHref) {
        diagramVisitor.visitDiagramDataInitialization([] as Set<Object>, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitScatterDiagram(pointImageHref)
    }

    void line(Set<Object> xDataList, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization(xDataList, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void line(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization([] as Set<Object>, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void area(Set<Object> xDataList, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization(xDataList, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitAreaDiagram()
    }

    void area(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization([] as Set<Object>, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitAreaDiagram()
    }

    void pie(boolean hasSlice, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
             BigDecimal widthInPx = null, BigDecimal heightInPx = null) {
        diagramVisitor.visitDiagramDataInitialization(["pieData"] as Set<Object>, widthInPx, heightInPx)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagram(hasSlice)
    }
}
