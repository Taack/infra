package taack.ui.diagram

import groovy.transform.CompileStatic

@CompileStatic
final class Chart {
    final String captation
    final String yCaptation
    final String[] labels
    List<ChartSeries> seriesList = []

    Chart(String captation, String yCaptation, String... labels) {
        this.captation = captation
        this.yCaptation = yCaptation
        this.labels = labels
    }

    void addSeries(ChartSeries series) {
        this.seriesList.add series
    }

    String render2d(BigDecimal width = 720.0, BigDecimal height = 480.0, boolean pdf = false) {
        def svg = new StringBuffer("""
        <svg id="graph" xmlns="http://www.w3.org/2000/svg" xlink="http://www.w3.org/1999/xlink"
        viewBox="0 0 ${width} ${height}" style="width:${width}px;height:${height}px;background-color:#f9ecec">
        """)

        seriesList.each {
            def xAxis = new StringBuffer("""<g transform="translate(30, ${height}) scale(1, -1)">""")
//            it ...
            xAxis.append("</g>")
            svg.append(xAxis)
        }

        svg.append("</svg>")

    }

    String renderPie(BigDecimal width = 720.0, BigDecimal height = 480.0, boolean pdf = false) {
    }

}
