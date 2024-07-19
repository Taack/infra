package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase)

    void visitDiagramPreparation(Set<Object> xDataList, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio radio)

    void dataset(String key, List<BigDecimal> yDataList)

    void dataset(String key, Map<Object, BigDecimal> dataMap)

    void visitBarDiagram(boolean isStacked)

    void visitScatterDiagram(String... pointImageHref)

    void visitLineDiagram()

    void visitAreaDiagram()

    void visitPieDiagram(boolean hasSlice)

    void visitDiagramEnd()
}