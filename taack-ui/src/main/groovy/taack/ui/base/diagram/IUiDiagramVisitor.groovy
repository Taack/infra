package taack.ui.base.diagram

import groovy.transform.CompileStatic

@CompileStatic
interface IUiDiagramVisitor {
    void visitDiagram()

    void visitDiagramEnd()

    void visitSvgDiagram(DiagramBaseSpec.HeightWidthRadio radio)

    void visitSvgDiagramEnd()

    void visitPngDiagram()

    void visitBarDiagram(List<String> xLabels, String title)

    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked)

    void visitLineDiagram(List<String> xLabels, String title)

    void visitLineDiagramEnd(String xTitle, String yTitle)

    void visitPieDiagram(String title)

    void visitPieDiagramEnd()

    void dataset(String key, List<BigDecimal> data)
}