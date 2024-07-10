package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitDiagramPreparation(List<String> xLabels, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitBarDiagram(boolean isStacked) {

    }

    @Override
    void visitLineDiagram() {

    }

    @Override
    void visitAreaDiagram(boolean isStacked) {

    }

    @Override
    void visitPieDiagram() {

    }

    @Override
    void dataset(String key, List<BigDecimal> data) {

    }
}
