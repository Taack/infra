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

    void bar(List<String> xLabels, boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(xLabels, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagram(isStacked)
    }

    void line(List<String> xLabels, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation(xLabels, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
    }

    void pie(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitDiagramPreparation([], radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagram()
    }
}
