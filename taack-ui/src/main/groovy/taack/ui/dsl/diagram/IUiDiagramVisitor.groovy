package taack.ui.dsl.diagram

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase)

    void visitDiagramEnd()

    void visitDiagramPreparation(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio)

    void visitBarDiagram(boolean isStacked)

    void visitLineDiagram()

    void visitPieDiagram()

    void dataset(String key, List<BigDecimal> data)
}