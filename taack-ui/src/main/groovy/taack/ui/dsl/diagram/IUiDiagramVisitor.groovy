package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase)

    void visitDiagramDataInitialization()

    void visitLabels(Number... labels)

    void visitLabels(String... labels)

    void dataset(String key, BigDecimal[] yDataList)

    void dataset(String key, Map<Object, BigDecimal> dataMap)

    void visitBarDiagram(boolean isStacked)

    void visitScatterDiagram(String... pointImageHref)

    void visitLineDiagram()

    void visitAreaDiagram()

    void visitPieDiagram(boolean hasSlice)

    void whiskersBoxData(String key, BigDecimal... boxData)

    void visitWhiskersDiagram()

    void visitDiagramEnd()

    void visitCustom(String html)

    void visitDiagramTabs()

    void visitDiagramTab(String i18n)

    void visitDiagramTabEnd()

    void visitDiagramTabsEnd()
}