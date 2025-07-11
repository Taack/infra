package taack.ui.diagram

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.document
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
import web.svg.SVGGElement
import web.svg.SVGSVGElement
import web.uievents.MouseEvent
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

        if (transformArea != null && s.querySelector("clipPath#clipSection") != null) { // todo: should change to "clipPath[id^='clipSection']" after modification of "RectBackgroundDiagramScene.clipSectionId"
            // Scroll
            s.onmousedown = EventHandler { e ->
                if (transformArea.isClientMouseInTransformArea(e.clientX.toDouble())) {
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
                if (transformArea.isClientMouseInTransformArea(e.clientX.toDouble())) {
                    e.preventDefault()
                    transformArea.zoom(translateX(e.clientX.toDouble()), e.deltaY < 0)
                }
            }

            // HoverLine and tooltip for LINE diagram
            s.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
                transformArea.refreshCurrentHoverLineAndDataToolTip(e.clientX.toDouble(), e.clientY.toDouble())
            })
            s.addEventListener(EventType("mouseleave"), EventHandler {
                transformArea.currentHoverLine?.remove()
                s.querySelectorAll(".diagram-tooltip").forEach { it.remove() }
            })
        }
    }

    fun translateX(x: Double): Double {
        val pt = s.createSVGPoint()
        pt.x = x
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
    }

    fun cloneLegendShape(dataset: String): SVGGElement {
        val cloned = document.createElement("g") as SVGGElement
        cloned.innerHTML = legends.find { it.dataset == dataset }?.g?.innerHTML ?: ""
        return cloned
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}