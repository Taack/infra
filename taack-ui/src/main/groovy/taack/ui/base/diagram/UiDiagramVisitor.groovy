package taack.ui.base.diagram

import groovy.transform.CompileStatic
import taack.ui.base.UiDiagramSpecifier

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitDiagramPreparation(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitBarDiagram(boolean isStacked) {

    }

    @Override
    void visitLineDiagram() {

    }

    @Override
    void visitPieDiagram() {

    }

    @Override
    void dataset(String key, List<BigDecimal> data) {

    }
}
