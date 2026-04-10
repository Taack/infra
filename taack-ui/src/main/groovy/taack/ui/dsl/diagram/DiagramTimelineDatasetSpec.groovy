package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramTimelineDatasetSpec {
    IUiDiagramVisitor diagramVisitor

    DiagramTimelineDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void dateFormat(DiagramXLabelDateFormat dateFormat) {
        diagramVisitor.visitLabels(dateFormat)
    }

    void dataset(final String key, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DiagramTimelinePeriodDataSpec) Closure closure) {
        closure.delegate = new DiagramTimelinePeriodDataSpec(key, diagramVisitor)
        closure.call()
    }

    void option(DiagramOption option) {
        diagramVisitor.visitDiagramOption(option)
    }
}
