package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramBaseSpec {
    final IUiDiagramVisitor diagramVisitor
    final DiagramTypeSpec diagramTypeSpec

    DiagramBaseSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
        this.diagramTypeSpec = new DiagramTypeSpec(diagramVisitor)
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

    void svg(HeightWidthRadio radio, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) Closure closure) {
        diagramVisitor.visitSvgDiagram(radio)
        closure.delegate = diagramTypeSpec
        closure.call()
    }

    void png(HeightWidthRadio radio, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) Closure closure) {
        diagramVisitor.visitPngDiagram(radio)
        closure.delegate = diagramTypeSpec
        closure.call()
    }
}
