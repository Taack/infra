package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
interface IUiDiagramVisitor {
    void setDiagramBase(UiDiagramSpecifier.DiagramBase diagramBase)

    enum DiagramType {
        BAR,
        SCATTER,
        LINE,
        AREA,
        PIE,
        WHISKERS,
        TIMELINE,
        CUSTOM_HTML
    }

    IUiDiagramVisitor visitDiagram(DiagramType diagramType, Map params, boolean isComboDiagram)

    void visitLabels(Number... labels)

    void visitLabels(String... labels)

    void visitLabels(Date... dates)

    void dataset(String key, BigDecimal[] yDataList)

    void dataset(String key, Map<Object, BigDecimal> dataMap)

    void dataset(String key, Date... dates)

    void whiskersBoxData(String key, BigDecimal... boxData)

    void timelinePeriodData(String key, String keyDescription, String keyImageHref, Date startDate, Date endDate, String title)

    void visitDiagramEnd()

    void visitDiagramOption(DiagramOption diagramOption)

    void visitCustom(String html)
}