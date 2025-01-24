package taack.ui.diagram

import js.array.asList
import taack.ui.base.element.AjaxBlock
import web.svg.SVGElement

class Diagram(val parent: AjaxBlock, val s: SVGElement) {
    companion object {
        fun getSiblingDiagram(p: AjaxBlock): List<Diagram> {
            val elements: List<*> = p.d.querySelectorAll("svg.taackDiagram").asList()
            return elements.map {
                Diagram(p, it as SVGElement)
            }
        }
    }

    private val legends: List<DiagramLegend> = DiagramLegend.getSiblingDiagramLegend(this)
    private val dataGroups: List<DiagramDataGroup> = DiagramDataGroup.getSiblingDiagramDataGroup(this) // datas at same xLabel

    init {
        s.style.userSelect = "none"
    }

    fun hideOrShowDataset(toShow: Boolean, dataset: String) {
        dataGroups.forEach {
            it.hideOrShowDataset(toShow, dataset)
        }
    }
}