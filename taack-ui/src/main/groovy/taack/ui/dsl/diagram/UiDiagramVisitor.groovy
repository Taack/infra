package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
class UiDiagramVisitor implements IUiDiagramVisitor {
    @Override
    void setDiagramBase(UiDiagramSpecifier.DiagramBase diagramBase) {

    }

    @Override
    UiDiagramVisitor visitDiagram(DiagramType diagramType, Map params, boolean isComboDiagram) {
        return null
    }

    @Override
    void visitLabels(Number... labels) {

    }

    @Override
    void visitLabels(String... labels) {

    }

    @Override
    void visitLabels(Date... dates) {

    }

    @Override
    void dataset(String key, BigDecimal... yDataList) {

    }

    @Override
    void dataset(String key, Map<Object, BigDecimal> dataMap) {

    }

    @Override
    void dataset(String key, Date... dates) {

    }

    @Override
    void whiskersBoxData(String key, BigDecimal... boxData) {

    }

    @Override
    void timelinePeriodData(String key, String keyDescription, String keyImageHref, Date startDate, Date endDate, String title) {

    }

    @Override
    void visitDiagramEnd() {

    }

    @Override
    void visitDiagramOption(DiagramOption diagramOption) {

    }

    @Override
    void visitCustom(String html) {

    }
}
