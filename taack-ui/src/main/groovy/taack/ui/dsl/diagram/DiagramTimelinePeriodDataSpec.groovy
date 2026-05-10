package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTimelinePeriodDataSpec {
    final String key
    String keyDescription
    String keyImageHref
    final IUiDiagramVisitor diagramVisitor

    DiagramTimelinePeriodDataSpec(final String key, final IUiDiagramVisitor diagramVisitor) {
        this.key = key
        this.keyDescription = null
        this.keyImageHref = null
        this.diagramVisitor = diagramVisitor
    }

    void keyDescription(String keyDescription) {
        this.keyDescription = keyDescription
    }

    void keyImageHref(String keyImageHref) {
        this.keyImageHref = keyImageHref
    }

    void periodData(Date startDate, Date endDate, String title = '') {
        diagramVisitor.timelinePeriodData(key, keyDescription, keyImageHref, startDate, endDate, title)
    }
}
