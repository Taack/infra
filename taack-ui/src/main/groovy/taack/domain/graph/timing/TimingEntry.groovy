package taack.domain.graph.timing

import groovy.transform.CompileStatic

@CompileStatic
class TimingEntry {
    String name
    List<TimingSeriesEntry> seriesEntries

    final Integer r = this.toString().hashCode()

    String getName() {
        name.replace('"', '\'')
    }

    String getAlias() {
        "TE${(name.hashCode().toString() + r.toString()).replaceAll('-', 'A')}"
    }
}
