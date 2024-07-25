package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {

    }

    @Override
    void visitDiagramDataInitialization(Set<Object> xDataList, BigDecimal widthInPx, BigDecimal heightInPx) {

    }

    @Override
    void dataset(String key, List<BigDecimal> yDataList) {

    }

    @Override
    void dataset(String key, Map<Object, BigDecimal> dataMap) {

    }

    @Override
    void visitBarDiagram(boolean isStacked) {

    }

    @Override
    void visitScatterDiagram(String... pointImageHref) {

    }

    @Override
    void visitLineDiagram() {

    }

    @Override
    void visitAreaDiagram() {

    }

    @Override
    void visitPieDiagram(boolean hasSlice) {

    }

    @Override
    void whiskersBoxData(String key, List<BigDecimal> boxData) {

    }

    @Override
    void visitWhiskersDiagram() {

    }

    @Override
    void visitDiagramEnd() {

    }
}
