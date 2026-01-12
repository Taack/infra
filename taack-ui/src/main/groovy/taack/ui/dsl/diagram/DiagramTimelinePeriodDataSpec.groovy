package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTimelinePeriodDataSpec {
    final String key
    final IUiDiagramVisitor diagramVisitor

    DiagramTimelinePeriodDataSpec(final String key, final IUiDiagramVisitor diagramVisitor) {
        this.key = key
        this.diagramVisitor = diagramVisitor
    }

    void periodData(Date startDate, Date endDate, String title = '') {
        diagramVisitor.timelinePeriodData(key, startDate, endDate, title)
    }
}
