package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase)

    void visitDiagramPreparation(List<String> xLabels, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio radio)

    void dataset(String key, List<BigDecimal> data)

    void visitBarDiagram(boolean isStacked)

    void visitLineDiagram()

    void visitAreaDiagram(boolean isStacked)

    void visitPieDiagram(boolean hasSlice)

    void visitDiagramEnd()
}