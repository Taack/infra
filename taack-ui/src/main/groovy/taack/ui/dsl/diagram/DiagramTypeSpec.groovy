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

    enum HeightWidthRadio {
        THREE(3.0),
        TWO(2.0),
        ONE(1.0),
        HALF(0.5),
        THIRD(1.0 / 3)

        HeightWidthRadio(final BigDecimal r) {
            this.radio = r
        }

        final BigDecimal radio
    }

    void bar(Set<Object> xDataList, boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
             BigDecimal widthInPx = null, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(xDataList, widthInPx, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagram(isStacked)
    }

    void line(Set<Object> xDataList, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(xDataList, widthInPx, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void line(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation([] as Set<Object>, widthInPx, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void area(Set<Object> xDataList, boolean isStacked = true,
              @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              BigDecimal widthInPx = null, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(xDataList, widthInPx, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitAreaDiagram(isStacked)
    }

    void pie(boolean hasSlice, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
             BigDecimal widthInPx = null, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(["pieData"] as Set<Object>, widthInPx, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagram(hasSlice)
    }
}
