package taack.ui.diagram

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.document
import web.events.EventHandler
import web.svg.*
import web.uievents.WheelEvent

class Diagram(val parent: AjaxBlock, val s: SVGSVGElement): BaseElement {
    companion object {
        fun getSiblingDiagram(p: AjaxBlock): List<Diagram> {
            val elements: List<*> = p.d.querySelectorAll("svg.taackDiagram").asList()
            return elements.map {
                Diagram(p, it as SVGSVGElement)
            }
        }
    }

    private var isScrolling: Boolean = false
    private var previousMouseX: Double? = null
    private val legends: List<DiagramLegend> = DiagramLegend.getSiblingDiagramLegend(this)
    val transformArea: DiagramTransformArea? = DiagramTransformArea.getSiblingDiagramTransformArea(this)

    init {
        s.style.userSelect = "none"

        if (transformArea != null && s.querySelector("clipPath#clipSection") != null) {
            // Scroll
            s.onmousedown = EventHandler { e ->
                if (transformArea.isClientMouseInTransformArea(e)) {
                    isScrolling = true
                    previousMouseX = translateX(e.clientX.toDouble())
                }
            }
            s.onmousemove = EventHandler { e ->
                if (isScrolling && previousMouseX != null) {
                    val currentMouseX = translateX(e.clientX.toDouble())
                    transformArea.scrollBy(currentMouseX - previousMouseX!!)
                    previousMouseX = currentMouseX
                }
            }
            s.onmouseup = EventHandler {
                isScrolling = false
            }
            s.onmouseleave = EventHandler {
                isScrolling = false
            }

            // Zoom
            s.onwheel = EventHandler { e: WheelEvent -> // e.deltaY < 0 : wheel up
                if (transformArea.isClientMouseInTransformArea(e)) {
                    e.preventDefault()
                    transformArea.zoom(translateX(e.clientX.toDouble()), e.deltaY < 0)
                }
            }
        }
    }

    fun translateX(x: Double): Double {
        val pt = s.createSVGPoint()
        pt.x = x
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
    }

    fun cloneLegendShape(dataset: String): SVGGElement {
        val cloned = document.createElement(SvgTagName("g")) as SVGGElement
        cloned.innerHTML = legends.find { it.dataset == dataset }?.g?.innerHTML ?: ""
        return cloned
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}