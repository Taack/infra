package taack.ui.diagram

import js.array.asList
import taack.ui.base.element.AjaxBlock
import web.dom.document
import web.svg.*

class Diagram(val parent: AjaxBlock, val s: SVGSVGElement) {
    companion object {
        fun getSiblingDiagram(p: AjaxBlock): List<Diagram> {
            val elements: List<*> = p.d.querySelectorAll("svg.taackDiagram").asList()
            return elements.map {
                Diagram(p, it as SVGSVGElement)
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

    fun getDiagramRoot(): Diagram {
        return this
    }

    fun cloneLegendShape(dataset: String): SVGGElement {
        val cloned = document.createElement(SvgTagName("g")) as SVGGElement
        cloned.innerHTML = legends.find { it.dataset == dataset }?.g?.innerHTML ?: ""
        return cloned
    }
}