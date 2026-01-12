package taack.ui.diagram

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.BaseElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
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

    private val fontSizePercentage: Double = s.attributes.getNamedItem("font-size-percentage")?.value?.toDouble() ?: 1.0
    val transformArea: DiagramTransformArea? = DiagramTransformArea.getSiblingDiagramTransformArea(this)

    init {
        s.style.userSelect = "none"
        DiagramLegend.getSiblingDiagramLegend(this)

        if (transformArea != null && s.querySelector("clipPath[id^='clipSection']") != null) {
            // Scroll
            var isScrollingX = false
            var previousMousePosition: Double? = null
            s.onmousedown = EventHandler { e ->
                if (transformArea.isClientMouseInTransformArea(e.clientX.toDouble())) {
                    isScrollingX = true
                    previousMousePosition = translateX(e.clientX.toDouble())
                }
            }
            s.onmousemove = EventHandler { e ->
                if (isScrollingX && previousMousePosition != null) {
                    val currentMouseX = translateX(e.clientX.toDouble())
                    transformArea.horizontalScrollBy(currentMouseX - previousMousePosition!!)
                    previousMousePosition = currentMouseX
                }
            }
            s.onmouseup = EventHandler {
                isScrollingX = false
            }
            s.onmouseleave = EventHandler {
                isScrollingX = false
            }

            var isWheeling = false
            var wheelTimer: Int? = null
            fun tryToReleaseWheelDefaultBehavior(e: WheelEvent) { // Release Wheel default behavior only after the wheeling has been stopped for >0.5s
                if (isWheeling) {
                    e.preventDefault()

                    if (wheelTimer != null) {
                        window.clearTimeout(wheelTimer!!)
                    }
                    wheelTimer = window.setTimeout({
                        isWheeling = false
                    }, 500)
                }
            }

            // Zoom
            s.onwheel = EventHandler { e: WheelEvent -> // e.deltaY < 0 : wheel up
                if (transformArea.isClientMouseInTransformArea(e.clientX.toDouble(), e.clientY.toDouble())) {
                    // possible to execute Zoom : Stop the page scroll
                    // no possible anymore to Zoom and has stopped the Wheel for >0.5s : Release the page scroll
                    if (transformArea.zoom(translateX(e.clientX.toDouble()), e.deltaY < 0)) {
                        e.preventDefault()
                        isWheeling = true
                    } else {
                        tryToReleaseWheelDefaultBehavior(e)
                    }
                }
            }

            // Vertical scroll for TIMELINE diagram
            if (transformArea.getShapeType() == "timeline") {
                s.addEventListener(EventType("wheel"), EventHandler { e: WheelEvent ->
                    if (transformArea.isClientMouseInYAxisLabelArea(e.clientX.toDouble(), e.clientY.toDouble())) {
                        if (transformArea.verticalScroll(e.deltaY < 0)) {
                            e.preventDefault()
                            isWheeling = true
                        } else {
                            tryToReleaseWheelDefaultBehavior(e)
                        }
                    }
                })

                var isScrollingY = false
                s.addEventListener(EventType("mousedown"), EventHandler { e: MouseEvent ->
                    if (transformArea.isClientMouseInYAxisLabelArea(e.clientX.toDouble())) {
                        isScrollingY = true
                        previousMousePosition = translateY(e.clientY.toDouble())
                    }
                })
                s.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
                    if (isScrollingY && previousMousePosition != null) {
                        val currentMouseY = translateY(e.clientY.toDouble())
                        transformArea.verticalScrollBy(currentMouseY - previousMousePosition!!)
                        previousMousePosition = currentMouseY
                    }
                })
                s.addEventListener(EventType("mouseup"), EventHandler { isScrollingY = false })
                s.addEventListener(EventType("mouseleave"), EventHandler { isScrollingY = false })
            }

            // HoverLine and tooltip for LINE diagram
            if (transformArea.getShapeType() == "line") {
                s.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
                    transformArea.refreshCurrentHoverLineAndDataToolTip(e)
                })
                s.addEventListener(EventType("mouseleave"), EventHandler {
                    if (transformArea.currentHoverLine != null) {
                        transformArea.currentHoverLine!!.remove()
                        s.querySelectorAll(".diagram-tooltip").forEach { it.remove() }
                    }
                })
            }
        }
    }

    fun translateX(x: Double): Double {
        val pt = s.createSVGPoint()
        pt.x = x
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
    }

    fun translateY(y: Double): Double {
        val pt = s.createSVGPoint()
        pt.y = y
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).y
    }

    fun getFontSizePercentage(): Double {
        return fontSizePercentage
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}