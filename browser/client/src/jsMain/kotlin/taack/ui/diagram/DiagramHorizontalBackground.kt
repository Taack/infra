package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.svg.*
import kotlin.math.*

class DiagramHorizontalBackground(private val parent: Diagram, private val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramHorizontalBackground(d: Diagram): List<DiagramHorizontalBackground> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='HORIZONTAL_BACKGROUND']").asList()
            return elements.map {
                DiagramHorizontalBackground(d, it as SVGGElement)
            }
        }
    }

    val areaMinY: Double = g.attributes.getNamedItem("area-min-y")!!.value.toDouble()
    val areaMaxY: Double = g.attributes.getNamedItem("area-max-y")!!.value.toDouble()
    private val horizontalBackgroundLines = g.querySelectorAll("line")?.asList() ?: listOf()
    private val gapHeight: Double = if (horizontalBackgroundLines.size > 1)
        horizontalBackgroundLines[1].getAttribute("y1")!!.toDouble() - horizontalBackgroundLines[0].getAttribute("y1")!!.toDouble()
    else (areaMaxY - areaMinY)

    fun verticalScroll(isUp: Boolean): Boolean { // move a fixed distance "gapHeight" each time
        return verticalScrollBy(if (isUp) gapHeight else -gapHeight)
    }

    fun verticalScrollBy(movingDistance: Double): Boolean {
        if (horizontalBackgroundLines.isNotEmpty()) {
            val y = parent.scrollY + movingDistance
            val minY = areaMaxY - round(horizontalBackgroundLines.last().getAttribute("y1")!!.toDouble())
            val maxY = 0.0
            val adjustedY = min(maxY, max(minY, y))
            if (adjustedY != parent.scrollY) {
                g.setAttribute("transform", "translate(0.0,${adjustedY})")
                parent.dataContainers.forEach { dataContainer ->
                    dataContainer.g.setAttribute("transform", "translate(${parent.scrollX},${adjustedY})")
                }
                parent.scrollBar?.scrollBy(-movingDistance)
                parent.scrollY = adjustedY
                return true
            }
        }
        return false
    }
}