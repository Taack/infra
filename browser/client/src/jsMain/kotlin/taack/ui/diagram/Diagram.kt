package taack.ui.diagram

import js.array.asList
import taack.ui.base.element.AjaxBlock
import web.dom.document
import web.events.EventHandler
import web.svg.*
import web.uievents.MouseEvent

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
    private val dataGroups: List<DiagramDataGroup> = DiagramDataGroup.getSiblingDiagramDataGroup(this)

    private var isScrolling: Boolean = false
    private var previousMouseX: Double? = null
    private val scrollArea = s.querySelector(".taackDiagramScroll")

    init {
        s.style.userSelect = "none"

        // Horizontal scroll
        if (scrollArea != null) {
            s.onmousedown = EventHandler { e ->
                isScrolling = true
                previousMouseX = getMouseX(e)
            }
            s.onmousemove = EventHandler { e ->
                if (isScrolling && previousMouseX != null) {
                    val currentMouseX = getMouseX(e)
                    val targetTransformX = (scrollArea.getAttribute("scroll-x")?.toDouble() ?: 0.0) + (currentMouseX - previousMouseX!!)
                    scrollArea.setAttribute("scroll-x", targetTransformX.toString())
                    scrollArea.setAttribute("transform", "translate(${targetTransformX},0.0)")
                    previousMouseX = currentMouseX
                }
            }
            s.onmouseup = EventHandler {
                isScrolling = false
            }
            s.onmouseleave = EventHandler {
                isScrolling = false
            }
        }
    }

    private fun getMouseX(e: MouseEvent): Double {
        val pt = s.createSVGPoint()
        pt.x = e.clientX.toDouble()
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
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

    fun getDiagramScrollX(): Double {
        return scrollArea?.getAttribute("scroll-x")?.toDouble() ?: 0.0
    }
}