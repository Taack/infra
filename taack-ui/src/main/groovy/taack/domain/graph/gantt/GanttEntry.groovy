package taack.domain.graph.gantt

import grails.util.Pair
import groovy.transform.CompileStatic

/**
 * Low level class to draw read-only Gantt diagrams (WiP)
 */
@CompileStatic
final class GanttEntry {
    private static String nameModifier(String title) {
        title.replace("[", "_").replace("]", "_")
    }

    /**
     * Constructor
     *
     * @param objectId The object ID represented by this entry
     * @param name The label printed
     * @param temporality Way duration of the Gantt Entry is defined (SEPARATOR: No duration, HAPPEN: relative duration)
     * @param start Start date
     * @param lastsDays Number of days the Gantt last is temporality parameter is LASTS_DAYS
     * @param color Gant entry color
     */
    GanttEntry(Long objectId, String name, Temporality temporality, Date start, Integer lastsDays = 0, Color color = null) {
        this.objectId = objectId
        this.name = nameModifier(name)
        this.color = color
        this.temporality = temporality
        this.lastsDays = lastsDays
        this.start = start
    }

    enum Color {
        CORAL_GREEN("Coral/Green"),
        GREENYELLOW_GREEN("GreenYellow/Green"),
        LAVENDER_BLUE("Lavender/LightBlue")

        Color(String colorString) {
            this.colorString = colorString
        }

        final String colorString
    }

    enum Relation {
        AFTER, BEFORE, IDENTICAL
    }

    /**
     * HAPPEN: The end date is the previous Gantt entry end date
     * LASTS_DAYS: The end date is start + lastInDays
     * SEPARATOR: Draw an horizontal line
     */
    enum Temporality {
        HAPPEN, LASTS_DAYS, SEPARATOR
    }
    final Long objectId
    final String name
    final Color color
    final Temporality temporality

    @Override
    String toString() {
        return "${name}, ${start}, $lastsDays, $temporality, $color"
    }
    String note
    Integer lastsDays
    Integer week
    Date start

    List<Pair<Relation, GanttEntry>> relations
}
