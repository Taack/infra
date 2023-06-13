package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram()

    void visitDiagramEnd()

    void visitBarDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio)

    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked)

    void visitLineDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio)

    void visitLineDiagramEnd(String xTitle, String yTitle)

    void visitPieDiagram(DiagramTypeSpec.HeightWidthRadio radio)

    void visitPieDiagramEnd()

    void dataset(String key, List<BigDecimal> data)
}