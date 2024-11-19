package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTypeSpec {
    final IUiDiagramVisitor diagramVisitor
    final DiagramDatasetSpec diagramDatasetSpec

    DiagramTypeSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
        this.diagramDatasetSpec = new DiagramDatasetSpec(diagramVisitor)
    }

    static Closure<DiagramTypeSpec> buildDiagramTypeSpec(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) final Closure closure) {
        closure
    }

    void inline(final Closure<DiagramTypeSpec> diagramTypeClosure) {
        diagramTypeClosure.delegate = this
        diagramTypeClosure.call()
    }

    void tabs(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) final Closure closure) {
        diagramVisitor.visitDiagramTabs()
        closure.delegate = this
        closure.call()
        diagramVisitor.visitDiagramTabsEnd()
    }

    void tab(final String i18n, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = DiagramTypeSpec) final Closure closure) {
        diagramVisitor.visitDiagramTab(i18n)
        closure.delegate = this
        closure.call()
        diagramVisitor.visitDiagramTabEnd()
    }

    void bar(boolean isStacked = true,
             @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitBarDiagram(isStacked)
        diagramVisitor.visitDiagramEnd()
    }

    void scatter(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure,
              String... pointImageHref) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitScatterDiagram(pointImageHref)
        diagramVisitor.visitDiagramEnd()
    }

    void line(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitLineDiagram()
        diagramVisitor.visitDiagramEnd()
    }

    void area(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitAreaDiagram()
        diagramVisitor.visitDiagramEnd()
    }

    void pie(boolean hasSlice, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = diagramDatasetSpec
        closure.call()
        diagramVisitor.visitPieDiagram(hasSlice)
        diagramVisitor.visitDiagramEnd()
    }

    void whiskers(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramWhiskersDatasetSpec) Closure closure) {
        diagramVisitor.visitDiagramDataInitialization()
        closure.delegate = new DiagramWhiskersDatasetSpec(diagramVisitor)
        closure.call()
        diagramVisitor.visitWhiskersDiagram()
        diagramVisitor.visitDiagramEnd()
    }

    void custom(String html) {
        diagramVisitor.visitDiagramDataInitialization()
        diagramVisitor.visitCustom(html)
    }
}
