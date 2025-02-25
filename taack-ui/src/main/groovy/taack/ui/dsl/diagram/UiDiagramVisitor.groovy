package taack.ui.dsl.diagram

import diagram.scene.DiagramXLabelDateFormat
import diagram.scene.RectBackgroundDiagramScene
import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {

    }

    @Override
    void visitDiagramDataInitialization() {

    }

    @Override
    void visitLabels(Number... labels) {

    }

    @Override
    void visitLabels(String... labels) {

    }

    @Override
    void visitLabels(DiagramXLabelDateFormat dateFormat, Date... dates) {

    }

    @Override
    void dataset(String key, BigDecimal... yDataList) {

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
    void whiskersBoxData(String key, BigDecimal... boxData) {

    }

    @Override
    void visitWhiskersDiagram() {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitCustom(String html) {

    }

    @Override
    void visitDiagramAction(String controller, String action, Long id, Map<String, ?> params) {

    }
}
