package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramTypeSpec {
    IUiDiagramVisitor diagramVisitor
    boolean isInsideCombo

    DiagramTypeSpec(final IUiDiagramVisitor diagramVisitor, final boolean isInsideCombo = false) {
        this.diagramVisitor = diagramVisitor
        this.isInsideCombo = isInsideCombo
    }

    static Closure<DiagramTypeSpec> buildDiagramTypeSpec(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) final Closure closure) {
        closure
    }

    void inline(final Closure<DiagramTypeSpec> diagramTypeClosure) {
        diagramTypeClosure.delegate = this
        diagramTypeClosure.call()
    }

    void bar(boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.BAR, [isStacked: isStacked], isInsideCombo)
        closure.delegate = new DiagramDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void scatter(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              String... pointImageHref) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.SCATTER, [pointImageHref: pointImageHref.toList()], isInsideCombo)
        closure.delegate = new DiagramDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void line(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.LINE, [:], isInsideCombo)
        closure.delegate = new DiagramDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void area(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.AREA, [:], isInsideCombo)
        closure.delegate = new DiagramDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void pie(boolean hasSlice = false, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.PIE, [hasSlice: hasSlice], isInsideCombo)
        closure.delegate = new DiagramDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void whiskers(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.WHISKERS, [:], isInsideCombo)
        closure.delegate = new DiagramWhiskersDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void timeline(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramTimelineDatasetSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.TIMELINE, [:], isInsideCombo)
        closure.delegate = new DiagramTimelineDatasetSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    /**
     * Organisation chart: a forest of boxes linked to their parent. Nodes whose parent is null or unknown are roots.
     * Sibling leaves are stacked vertically under their parent to keep wide teams readable.
     */
    void orgChart(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramOrgChartSpec) Closure closure) {
        IUiDiagramVisitor d = diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.ORG_CHART, [:], isInsideCombo)
        closure.delegate = new DiagramOrgChartSpec(d)
        closure.call()
        d.visitDiagramEnd()
    }

    void custom(String html) {
        diagramVisitor.visitDiagram(IUiDiagramVisitor.DiagramType.CUSTOM_HTML, [:], false)
        diagramVisitor.visitCustom(html)
    }
}
