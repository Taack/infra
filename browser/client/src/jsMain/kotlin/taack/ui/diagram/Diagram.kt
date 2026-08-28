package taack.ui.diagram

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.document
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
import web.mouse.MouseEvent
import web.mouse.WheelEvent
import web.svg.*
import kotlin.math.abs

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
    val tooltips: List<DiagramTooltip> = DiagramTooltip.getSiblingDiagramTooltip(this)
    val dataContainers: List<DiagramDataContainer> = DiagramDataContainer.getSiblingDiagramDataContainer(this)
    val horizontalBackgrounds: List<DiagramHorizontalBackground> = DiagramHorizontalBackground.getSiblingDiagramHorizontalBackground(this)
    val verticalBackground: DiagramVerticalBackground? = DiagramVerticalBackground.getSiblingDiagramVerticalBackground(this)
    val scrollBar: DiagramScrollBar? = DiagramScrollBar.getSiblingDiagramScrollBar(this)

    var scrollX: Double = 0.0
    var scrollY: Double = 0.0

    init {
        s.style.userSelect = "none"
        DiagramLegend.getSiblingDiagramLegend(this)

        if (dataContainers.isNotEmpty() && verticalBackground != null && horizontalBackgrounds.isNotEmpty()) {
            // Scroll
            var isScrollingX = false
            var previousMousePosition: Double? = null
            s.onmousedown = EventHandler { e ->
                if (isClientMouseInDataContainerArea(e.clientX.toDouble(), e.clientY.toDouble())) {
                    isScrollingX = true
                    previousMousePosition = translateX(e.clientX.toDouble())
                }
            }
            s.onmousemove = EventHandler { e ->
                if (isScrollingX && previousMousePosition != null) {
                    val currentMouseX = translateX(e.clientX.toDouble())
                    verticalBackground.horizontalScrollBy(currentMouseX - previousMousePosition!!)
                    previousMousePosition = currentMouseX
                }
            }
            s.onmouseup = EventHandler {
                isScrollingX = false
            }
            s.onmouseleave = EventHandler {
                isScrollingX = false
            }

            // Zoom
            s.onwheel = EventHandler { e: WheelEvent -> // e.deltaY < 0 : wheel up
                if (e.ctrlKey) {
                    e.preventDefault()
                    verticalBackground.horizontalZoom(if (e.deltaY < 0) 1.1 else 0.9, translateX(e.clientX.toDouble()))
                }
            }

            // Vertical scroll for TIMELINE diagram
            if (dataContainers.find { it.getShapeType() == "timeline" } != null) {
                s.addEventListener(EventType("wheel"), EventHandler { e: WheelEvent ->
                    if (!e.ctrlKey) {
                        horizontalBackgrounds.forEach { horizontalBackground ->
                            if (horizontalBackground.verticalScroll(e.deltaY < 0)) {
                                e.preventDefault()
                            }
                        }
                    }
                })

                var isScrollingY = false
                var isScrollingByBar = false
                s.addEventListener(EventType("mousedown"), EventHandler { e: MouseEvent ->
                    if (isClientMouseInYAxisLabelArea(e.clientX.toDouble())) {
                        isScrollingY = true
                        isScrollingByBar = false
                        previousMousePosition = translateY(e.clientY.toDouble())
                    }
                })
                scrollBar?.g?.onmousedown = EventHandler { e ->
                    isScrollingY = true
                    isScrollingByBar = true
                    previousMousePosition = translateY(e.clientY.toDouble())
                }
                document.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
                    if (isScrollingY && previousMousePosition != null) {
                        val currentMouseY = translateY(e.clientY.toDouble())
                        val movingDistance = if (isScrollingByBar) (previousMousePosition!! - currentMouseY) / scrollBar!!.rate else currentMouseY - previousMousePosition!!
                        horizontalBackgrounds.forEach { horizontalBackground ->
                            horizontalBackground.verticalScrollBy(movingDistance)
                        }
                        previousMousePosition = currentMouseY
                    }
                })
                document.addEventListener(EventType("mouseup"), EventHandler {
                    isScrollingY = false
                    isScrollingByBar = false
                })
            }

            // HoverLine and tooltip for LINE diagram
            if (dataContainers.size == 1 && dataContainers.first().getShapeType() == "line" && tooltips.isNotEmpty()) {
                val currentHoverLine: SVGLineElement = document.createElementNS("http://www.w3.org/2000/svg", "line") as SVGLineElement
                currentHoverLine.setAttribute("y1", horizontalBackgrounds.first().areaMinY.toString())
                currentHoverLine.setAttribute("y2", horizontalBackgrounds.first().areaMaxY.toString())
                currentHoverLine.setAttribute("style", "stroke:rgb(180, 180, 180);stroke-width:${1.3 * fontSizePercentage};pointer-events: none;")

                fun removeHoverLine() {
                    currentHoverLine.remove()
                    s.querySelectorAll(".diagram-tooltip").forEach { it.remove() }
                }

                s.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
                    removeHoverLine()
                    if (isClientMouseInDataContainerArea(e.clientX.toDouble(), e.clientY.toDouble())) {
                        val mouseX = translateX(e.clientX.toDouble())
                        var closestTooltipsByX: List<DiagramTooltip> = listOf() // may have several tooltips at same X point (They are from different datasets)
                        tooltips.groupBy {
                            val data = it.g.querySelector("g[element-type='DATA']")
                            if (data != null && (data as SVGGElement).style.display != "none") data.querySelector("circle")?.getAttribute("cx") else null
                        }.filter { it.key != null }.forEach {
                            if (closestTooltipsByX.isEmpty()) {
                                closestTooltipsByX = it.value
                            } else {
                                val originBBox = closestTooltipsByX.first().g.getBBox()
                                val bBox = it.value.first().g.getBBox()
                                if (abs(mouseX - (bBox.x + bBox.width / 2 + scrollX)) < abs(mouseX - (originBBox.x + originBBox.width / 2 + scrollX))) {
                                    closestTooltipsByX = it.value
                                }
                            }
                        }
                        if (closestTooltipsByX.isNotEmpty()) {
                            val cx = closestTooltipsByX.first().g.querySelector("circle")!!.getAttribute("cx")!!
                            currentHoverLine.setAttribute("x1", cx)
                            currentHoverLine.setAttribute("x2", cx)
                            dataContainers.first().g.appendChild(currentHoverLine)

                            val mouseY = translateY(e.clientY.toDouble())
                            var closestTooltipsByY = closestTooltipsByX.first()
                            closestTooltipsByX.forEach {
                                val originBBox = closestTooltipsByY.g.getBBox()
                                val bBox = it.g.getBBox()
                                if (abs(mouseY - (bBox.y + bBox.height / 2 + scrollY)) < abs(mouseY - (originBBox.y + originBBox.height / 2 + scrollY))) {
                                    closestTooltipsByY = it
                                }
                            }
                            closestTooltipsByY.showTooltip(e)
                        }
                    }
                })
                s.addEventListener(EventType("mouseleave"), EventHandler {
                    removeHoverLine()
                })
                s.addEventListener(EventType("wheel"), EventHandler {
                    removeHoverLine()
                })
            }
        }
    }

    fun translateX(x: Double): Double {
        val pt = s.createSVGPoint()
        pt.x = x
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).x
    }

    private fun translateY(y: Double): Double {
        val pt = s.createSVGPoint()
        pt.y = y
        return pt.matrixTransform(s.getScreenCTM()!!.inverse()).y
    }

    private fun isClientMouseInDataContainerArea(mouseClientX: Double, mouseClientY: Double): Boolean {
        return translateX(mouseClientX) in verticalBackground!!.areaMinX..verticalBackground.areaMaxX && translateY(mouseClientY) in horizontalBackgrounds.first().areaMinY..horizontalBackgrounds.first().areaMaxY
    }

    private fun isClientMouseInYAxisLabelArea(mouseClientX: Double): Boolean {
        return translateX(mouseClientX) in 0.0..verticalBackground!!.areaMinX
    }

    fun getFontSizePercentage(): Double {
        return fontSizePercentage
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}