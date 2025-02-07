package taack.ui.diagram

import js.array.asList
import taack.ui.base.element.AjaxBlock
import web.dom.document
import web.events.EventHandler
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

    private var isScrolling: Boolean = false
    private var previousMouseX: Double? = null
    private val legends: List<DiagramLegend> = DiagramLegend.getSiblingDiagramLegend(this)
    val transformArea: DiagramTransformArea? = DiagramTransformArea.getSiblingDiagramTransformArea(this)

    init {
        s.style.userSelect = "none"

        if (transformArea != null) {
            // Scroll
            s.onmousedown = EventHandler { e ->
                isScrolling = true
                previousMouseX = translateX(e.clientX.toDouble())
            }
            s.onmousemove = EventHandler { e ->
                if (isScrolling && previousMouseX != null) {
                    val currentMouseX = translateX(e.clientX.toDouble())
                    transformArea.scroll(currentMouseX - previousMouseX!!)
                    previousMouseX = currentMouseX
                }
            }
            s.onmouseup = EventHandler {
                isScrolling = false
            }
            s.onmouseleave = EventHandler {
                isScrolling = false
            }

//            // Zoom: handle the number of background vertical lines, and then calculate zoom radio to move all elements
//            val areaMinX = transformArea.getAttribute("area-min-x")?.toDouble()
//            val areaMaxX = transformArea.getAttribute("area-max-x")?.toDouble()
//            val lines = transformArea.querySelectorAll("g[element-type='VERTICAL_BACKGROUND']>line").asList()
//            if (lines.isNotEmpty() && areaMinX != null && areaMaxX != null && areaMaxX > areaMinX) {
//                s.onwheel = EventHandler { e: WheelEvent -> // e.deltaY > 0 : wheel down
//                    e.preventDefault()
//                    val currentMinLineIndex = lines.indexOf(lines.find { line -> translateX(line.getBoundingClientRect().x) >= areaMinX } ?: lines.first())
//                    val currentMaxLineIndex = lines.indexOf(lines.findLast { line -> translateX(line.getBoundingClientRect().x) <= areaMaxX } ?: lines.last())
//                    val changeGap = max(1, (currentMaxLineIndex - currentMinLineIndex) / 10)
//                    val newMinLineIndex = max(0, if (e.deltaY > 0) currentMinLineIndex - changeGap else currentMinLineIndex + changeGap)  // move this line as new first background vertical line
//                    val newMaxLineIndex = min(lines.size - 1, if (e.deltaY > 0) currentMaxLineIndex + changeGap else currentMaxLineIndex - changeGap) // move this line as new last background vertical line
//                    if (newMinLineIndex < newMaxLineIndex && (currentMinLineIndex != newMinLineIndex || currentMaxLineIndex != newMaxLineIndex)) {
//                        val newMinLine = lines[newMinLineIndex] as SVGLineElement // will change "x" value to "areaMinX"
//                        val newMaxLine = lines[newMaxLineIndex] as SVGLineElement // will change "x" value to "areaMaxX"
//                        val zoomRadio = (areaMaxX - areaMinX) / (newMaxLine.x1.baseVal.value - newMinLine.x1.baseVal.value)
//
//
//
//                    }
//                }
//            }
        }
    }

    private fun translateX(x: Double): Double {
        val pt = s.createSVGPoint()
        pt.x = x
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
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