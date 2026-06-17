package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class DiagramTimelinePeriodDataSpec {
    final String key
    String keyDescription
    List<String> keyImageHref
    final IUiDiagramVisitor diagramVisitor

    DiagramTimelinePeriodDataSpec(final String key, final IUiDiagramVisitor diagramVisitor) {
        this.key = key
        this.keyDescription = null
        this.keyImageHref = []
        this.diagramVisitor = diagramVisitor
    }

    void keyDescription(String keyDescription) {
        this.keyDescription = keyDescription
    }

    void keyImageHref(String keyImageHref) {
        this.keyImageHref.add(keyImageHref)
    }

    void periodData(Date startDate, Date endDate, String title = '') {
        diagramVisitor.timelinePeriodData(key, keyDescription, keyImageHref.join(','), startDate, endDate, title)
    }
}
