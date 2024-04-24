package taack.domain

import grails.compiler.GrailsCompileStatic
import org.springframework.beans.factory.annotation.Autowired
import taack.domain.graph.gantt.GanttEntry
import taack.domain.graph.timing.TimingEntry
import taack.ui.TaackUiConfiguration
import taack.utils.DateFormat

/**
 * Service allowing to draw read-only Gantt and Timing diagram using PlantUML
 */
@GrailsCompileStatic
final class TaackUMLService {

    static lazyInit = true
    static scope = "request"

    @Autowired
    TaackUiConfiguration taackUiConfiguration

    private String svg(String graph) {
        String[] cmd = [taackUiConfiguration.javaPath, "-jar", taackUiConfiguration.plantUmlPath, "-headless", "-charset", "UTF-8", "-tsvg", "-pipe"]
        Process p = new ProcessBuilder(cmd).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        p.outputStream << graph
        p.outputStream.flush()
        p.outputStream.close()
        p.inputStream.text
    }

    /**
     * Draw SVG Gantt from a list of Gatt entries
     *
     * @param ganttEntries Entries
     * @param flat If true, tries to draw Gantt entries on the same line
     * @return The SVG diagram
     */
    String ganttSvg(final List<GanttEntry> ganttEntries, boolean flat = false) {
        StringBuffer graph = new StringBuffer()
        graph.append("@startuml\n")
        GanttEntry previous = null
        if (!ganttEntries || ganttEntries.empty) {
            graph.append("(Empty Gantt) \n")
        } else {
            //def d = ganttEntries.sort { it.start }.first().start
            boolean wasSeparator = false
            Long currentId = null
            for (def ge in ganttEntries) {
                if (!currentId) currentId = ge.objectId
                if (previous && ge.temporality == GanttEntry.Temporality.LASTS_DAYS) {
                    graph.append((flat || wasSeparator) ? "" : "then ")
                    wasSeparator = false
                } else if (previous && ge.temporality == GanttEntry.Temporality.HAPPEN) graph.append("\n")
                else if ((!previous && ge.start)) {
                    //if (!previous) graph.append("Language ${LocaleContextHolder.locale.language}\n")
                    if (ge.start) graph.append("Project starts on ${DateFormat.format(ge.start, 'yyyy-MM-dd')}\n")
                }
                if (ge.temporality == GanttEntry.Temporality.SEPARATOR) {
                    graph.append("-- ${ge.name} --\n")
                    wasSeparator = true
                } else if (ge.temporality == GanttEntry.Temporality.LASTS_DAYS) {
                    graph.append("[${ge.name}]${ge.start ? " starts ${DateFormat.format(ge.start,'yyyy-MM-dd')} and " : " "} lasts ${ge.lastsDays} days\n")
                } else if (previous && ge.temporality == GanttEntry.Temporality.HAPPEN) {
                    graph.append("[${ge.name}]${ge.start ? " starts ${DateFormat.format(ge.start,'yyyy-MM-dd')} and " : " "} happens after [${previous.name}]'s end\n")
                }
                previous = ge
            }
        }
        graph.append("@enduml")
        log.info "GANTT ${graph.toString()}"
        svg(graph.toString())
    }

    /**
     * Draw an SVG timing diagram
     *
     * @param timingEntries Entries
     * @return The SVG timing diagram
     */
    String timingSvg(List<TimingEntry> timingEntries) {
        StringBuffer graph = new StringBuffer()
        graph.append("@startuml\n")
        if (!timingEntries || timingEntries.empty) {
            graph.append("(Empty Timing Diagram) \n")
        } else {
            for (def te in timingEntries) {
                graph.append("""concise "${te.name}" as ${te.alias} \n""")
            }
            for (def te in timingEntries) {
                graph.append("@${te.alias}\n")
                for (def s in te.seriesEntries) {
                    graph.append("${DateFormat.format(s.date, 'yyyy/MM/dd')} is ${s.status}\n")
                }
            }
        }
        graph.append("@enduml")
        log.info "TIMING ${graph.toString()}"
        svg(graph.toString())

    }

    /**
     * Draw an SVG timing diagram
     *
     * @param timingEntry Entry
     * @return The SVG timing diagram
     */
    String timingSvg(TimingEntry timingEntry) {
        StringBuffer graph = new StringBuffer()
        graph.append("@startuml\n")
        if (!timingEntry) {
            graph.append("(Empty Timing Diagram) \n")
        } else {
            graph.append("""concise "${timingEntry.name}" as ${timingEntry.alias} \n""")
            graph.append("@${timingEntry.alias}\n")

            for (def s in timingEntry.seriesEntries) {
                graph.append("${DateFormat.format(s.date, 'yyyy/MM/dd')} is ${s.status}\n")
            }
        }
        graph.append("@enduml")
        log.info "TIMING ${graph.toString()}"
        svg(graph.toString())
    }
}
