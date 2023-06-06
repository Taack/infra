package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram()

    void visitDiagramEnd()

    void visitSvgDiagram(DiagramBaseSpec.HeightWidthRadio radio)

    void visitPngDiagram(DiagramBaseSpec.HeightWidthRadio radio)

    void visitBarDiagram(List<String> xLabels)

    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked)

    void visitLineDiagram(List<String> xLabels)

    void visitLineDiagramEnd(String xTitle, String yTitle)

    void visitPieDiagram()

    void visitPieDiagramEnd()

    void dataset(String key, List<BigDecimal> data)
}