package taack.domain

import taack.domain.graph.gantt.GanttEntry

interface IDomainToGantt {
    List<GanttEntry> extractGantt()
}