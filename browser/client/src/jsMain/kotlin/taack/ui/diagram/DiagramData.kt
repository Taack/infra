package taack.ui.diagram

import js.array.asList
import web.dom.document
import web.events.EventHandler
import web.svg.*

class DiagramData(private val parent: DiagramDataGroup, val g: SVGGElement) {
    companion object {
        fun getSiblingDiagramData(dataGroup: DiagramDataGroup): List<DiagramData> {
            val elements: List<*> = dataGroup.g.querySelectorAll("g[element-type='DATA']").asList()
            return elements.map {
                DiagramData(dataGroup, it as SVGGElement)
            }
        }
    }

    val dataset: String = g.attributes.getNamedItem("dataset")!!.value
    private val shapes: List<SVGElement> = g.children.asList().filter { it.tagName != "text" }.unsafeCast<List<SVGElement>>()
    private val tooltip: SVGGElement?

    init {
        val tooltipLabel = g.getAttribute("data-label")
        if (tooltipLabel != null) {
            tooltip = document.createElement(SvgTagName("g"))

            val background: SVGPolygonElement = document.createElement(SvgTagName("polygon"))
            background.style.fill = "#00000090"
            tooltip.appendChild(background)

            val legend: SVGGElement = getDiagramRoot().cloneLegendShape(dataset)
            legend.querySelectorAll("text").forEach { (it as SVGTextElement).style.fill = "white" }
            legend.setAttribute("transform", "translate(0,-15)")
            tooltip.appendChild(legend)

            val value: SVGTextElement = document.createElement(SvgTagName("text"))
            value.setAttribute("text-rendering", "optimizeLegibility")
            value.setAttribute("style", "font-size: 13px; font-family: sans-serif; fill: white")
            value.innerHTML = tooltipLabel
            value.setAttribute("transform", "translate(0,15)")
            tooltip.appendChild(value)

            g.onmouseenter = EventHandler {
                getDiagramRoot().s.appendChild(tooltip)

                if (background.getAttribute("points") == null) {
                    val contentWidth = tooltip.getBBox().width
                    background.setAttribute("points", "${-contentWidth / 2 - 20},0 ${-contentWidth / 2 - 10},10 ${-contentWidth / 2 - 10},25 ${contentWidth / 2 + 10},25 ${contentWidth / 2 + 10},-25 ${-contentWidth / 2 - 10},-25 ${-contentWidth / 2 - 10},-10")
                }
                if (g.getBBox().x < getDiagramRoot().s.viewBox.baseVal.width / 2) {
                    background.setAttribute("transform", "translate(${(background.getBBox().width - 30) / 2},0)")
                    tooltip.setAttribute("transform", "translate(${g.getBBox().x + g.getBBox().width + 20},${g.getBBox().y + (if (shapes.firstOrNull()?.tagName == "circle") g.getBBox().height / 2.0 else 0.0)})")
                } else {
                    background.setAttribute("transform", "scale(-1,1) translate(${-(background.getBBox().width - 30) / 2},0)")
                    tooltip.setAttribute("transform", "translate(${g.getBBox().x - (tooltip.getBBox().width - 10)},${g.getBBox().y + (if (shapes.firstOrNull()?.tagName == "circle") g.getBBox().height / 2.0 else 0.0)})")
                }
            }
            g.onmouseleave = EventHandler {
                getDiagramRoot().s.removeChild(tooltip)
            }
        } else {
            tooltip = null
        }
    }

    fun hideOrShow(toShow: Boolean) {
        g.style.display = if (toShow) "" else "none"
    }

    fun moveShapeHorizontally(startX: Double, shapeWidth: Double) {
        shapes.forEach { shape ->
            if (shape.tagName == "rect") {
                shape.setAttribute("x", startX.toString())
                shape.setAttribute("width", shapeWidth.toString())
            } else if (shape.tagName == "line") {
                val x1 = shape.getAttribute("x1")
                val x2 = shape.getAttribute("x2")
                val y1 = shape.getAttribute("y1")
                val y2 = shape.getAttribute("y2")

                // just for the lines from whiskers shape, but not a generic solution...
                if (x1 == x2) { // vertical line
                    shape.setAttribute("x1", (startX + shapeWidth / 2).toString())
                    shape.setAttribute("x2", (startX + shapeWidth / 2).toString())
                } else if (y1 == y2) { // horizontal line
                    shape.setAttribute("x1", startX.toString())
                    shape.setAttribute("x2", (startX + shapeWidth).toString())
                }
            }
        }
    }

    fun moveShapeVertically(bottomY: Double): Double {
        val barHeight = shapes.firstOrNull()?.getAttribute("height")?.toDouble()
        if (barHeight != null) {
            val y = bottomY - barHeight
            shapes.first().setAttribute("y", y.toString())
            return y
        } else {
            return bottomY
        }
    }

    private fun getDiagramRoot(): Diagram {
        return parent.getDiagramRoot()
    }
}