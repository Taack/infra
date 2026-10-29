package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramOrgChartSpec {
    IUiDiagramVisitor diagramVisitor

    DiagramOrgChartSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    /**
     * Adds a box to the chart.
     *
     * @param key unique identifier of the node
     * @param parentKey key of the parent node, null for a root
     * @param title main line of the box
     * @param subtitle optional line below the title
     * @param header optional small line above the title
     * @param image optional picture drawn on top of the box, kept within a square: an href in SVG, a local file path in PNG.
     * A box without image shows the initials of its title as soon as another box of the chart has one.
     * With DiagramOption click action, clicking the box calls the action with the key as the x parameter.
     */
    void node(final String key, final String parentKey, final String title, final String subtitle = null, final String header = null, final String image = null) {
        diagramVisitor.orgChartNode(key, parentKey, title, subtitle, header, image)
    }

    void option(DiagramOption option) {
        diagramVisitor.visitDiagramOption(option)
    }
}
