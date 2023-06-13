package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void visitDiagram() {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitBarDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked) {

    }

    @Override
    void visitLineDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitLineDiagramEnd(String xTitle, String yTitle) {

    }

    @Override
    void visitPieDiagram(DiagramTypeSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitPieDiagramEnd() {

    }

    @Override
    void dataset(String key, List<BigDecimal> data) {

    }
}
