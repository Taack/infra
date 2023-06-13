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

    void bar(List<String> xLabels, String xTitle = "", String yTitle = "", boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitBarDiagram(xLabels, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagramEnd(xTitle, yTitle, isStacked)
    }

    void line(List<String> xLabels, String xTitle = "", String yTitle = "",
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitLineDiagram(xLabels, radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagramEnd(xTitle, yTitle)
    }

    void pie(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramDatasetSpec) Closure closure, HeightWidthRadio radio) {
        diagramVisitor.visitPieDiagram(radio)
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagramEnd()
    }
}
