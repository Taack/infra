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
    void visitSvgDiagram(DiagramBaseSpec.HeightWidthRadio radio) {

    }

    @Override
    void visitSvgDiagramEnd() {

    }

    @Override
    void visitPngDiagram() {

    }

    @Override
    void visitBarDiagram(List<String> xLabels, String title) {

    }

    @Override
    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked) {

    }

    @Override
    void visitLineDiagram(List<String> xLabels, String title) {

    }

    @Override
    void visitLineDiagramEnd(String xTitle, String yTitle) {

    }

    @Override
    void visitPieDiagram(String title) {

    }

    @Override
    void visitPieDiagramEnd() {

    }

    @Override
    void dataset(String key, List<BigDecimal> data) {

    }
}
