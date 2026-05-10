package taack.ui.diagram

import taack.ui.base.LeafElement
import web.svg.SVGGElement
import kotlin.math.max
import kotlin.math.min

class DiagramScrollBar(private val parent: Diagram, val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramScrollBar(d: Diagram): DiagramScrollBar? {
            val g = d.s.querySelector("g[element-type='VERTICAL_SCROLL_BAR']")
            return if (g != null) DiagramScrollBar(d, g as SVGGElement) else null
        }
    }

    private val scrollBarHeight = g.querySelector("rect")!!.getAttribute("height")!!.toDouble()
    private val totalHeight = parent.transformArea!!.areaMaxY - parent.transformArea.areaMinY
    val rate = scrollBarHeight / totalHeight

    fun scrollBy(movingDistance: Double) {
        val currentY = g.getAttribute("scroll-y")?.toDouble() ?: 0.0
        val y = currentY + movingDistance * rate
        val adjustedY = min(totalHeight - scrollBarHeight, max(0.0, y))
        if (adjustedY != currentY) {
            g.setAttribute("scroll-y", adjustedY.toString())
            g.setAttribute("transform", "translate(0.0,${adjustedY})")
        }
    }
}