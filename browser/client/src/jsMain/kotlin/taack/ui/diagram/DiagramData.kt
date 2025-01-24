package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.svg.SVGElement
import web.svg.SVGGElement

class DiagramData(private val parent: DiagramDataGroup, val g: SVGGElement) : LeafElement {
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
}