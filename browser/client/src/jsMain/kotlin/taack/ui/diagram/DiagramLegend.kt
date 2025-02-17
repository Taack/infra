package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.events.EventHandler
import web.svg.SVGGElement
import web.svg.SVGTextElement


class DiagramLegend(private val parent: Diagram, val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramLegend(d: Diagram): List<DiagramLegend> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='LEGEND']").asList()
            return elements.map {
                DiagramLegend(d, it as SVGGElement)
            }
        }
    }

    val dataset: String = g.attributes.getNamedItem("dataset")!!.value
    private val text: SVGTextElement = g.querySelector("text") as SVGTextElement
    private var isHidden: Boolean = false

    init {
        g.style.cursor = "pointer"
        g.onclick = EventHandler{
            text.style.textDecoration = if (isHidden) "" else "line-through"
            parent.transformArea?.hideOrShowDataset(isHidden, dataset)
            isHidden = !isHidden
        }
    }
}