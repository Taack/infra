package taack.ui.dsl.diagram

import groovy.transform.CompileStatic

@CompileStatic
class DiagramDatasetSpec {
    IUiDiagramVisitor diagramVisitor

    DiagramDatasetSpec(final IUiDiagramVisitor diagramVisitor) {
        this.diagramVisitor = diagramVisitor
    }

    void labels(Number... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(String... labels) {
        diagramVisitor.visitLabels(labels)
    }

    void labels(DiagramXLabelDateFormat dateFormat = DiagramXLabelDateFormat.DAY, Date... dates) {
        diagramVisitor.visitLabels(dateFormat, dates)
    }

    void dataset(final String key, final BigDecimal... yDataList) {
        diagramVisitor.dataset(key, yDataList)
    }

    void dataset(final String key, final BigDecimal yData) {
        diagramVisitor.dataset(key, yData)
    }

    void dataset(final String key, final Map<Object, BigDecimal> dataMap) {
        diagramVisitor.dataset(key, dataMap)
    }

    /**
     * Group the given dates according to DiagramXLabelDateFormat, then count every group and put the result as data to draw.
     *
     * For example:
     *      Knowing DiagramXLabelDateFormat.MONTH (Defined by {@link #labels(DiagramXLabelDateFormat dateFormat)}),
     *      and being given dates: 2025-01-01, 2025-01-10, 2025-01-20, 2025-02-01, 2025-02-10, 2025-03-01.
     *
     *      So they are divided to 3 groups with their own count: [2025-01: 3, 2025-02: 2, 2025-03: 1].
     *      The groups will be used as data to draw:
     *          - xLabels = ['2025-01', '2025-02', '2025-03']
     *          - yDataList = [3.0, 2.0, 1.0]
     *
     * @param key
     * @param dates
     */
    void dataset(final String key, final Date... dates) {
        diagramVisitor.dataset(key, dates)
    }

    void option(DiagramOption option) {
        diagramVisitor.visitDiagramOption(option)
    }
}
