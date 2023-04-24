package taack.ui.diagram

import groovy.transform.CompileStatic

@CompileStatic
class ChartSeries {
    enum Mode {
        PIE, BAR, LINE
    }

    final Mode mode
    final BigDecimal[] values

    ChartSeries(Mode mode = Mode.BAR, BigDecimal... values) {
        this.mode = mode
        this.values = values
    }
}
