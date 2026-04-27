package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.cssom.ClassName
import web.dom.document
import web.events.EventHandler
import web.geometry.DOMRect
import web.svg.*
import web.uievents.MouseEvent

class DiagramTooltip(private val parent: Diagram, val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramTooltip(d: Diagram): List<DiagramTooltip> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='TOOLTIP']").asList()
            return elements.map {
                DiagramTooltip(d, it as SVGGElement)
            }
        }
    }

    private val keyLabel: String = g.attributes.getNamedItem("key-label")!!.value
    private val keyColor: String = g.attributes.getNamedItem("key-color")?.value?.ifBlank { "white" } ?: "white"
    private val keyDescription: String = g.attributes.getNamedItem("key-description")?.value ?: ""
    private val keyImageHref: String = g.attributes.getNamedItem("key-image-href")?.value ?: ""
    private val xScrolled: Boolean = g.attributes.getNamedItem("x-scrolled")?.value?.toBoolean() ?: true
    private val yScrolled: Boolean = g.attributes.getNamedItem("y-scrolled")?.value?.toBoolean() ?: true
    private val tooltip: SVGGElement

    init {
        g.querySelectorAll("text").forEach { (it as SVGTextElement).style.pointerEvents = "unset" }

        val fontSizePercentage = parent.getFontSizePercentage()
        tooltip = document.createElementNS("http://www.w3.org/2000/svg", "g") as SVGGElement
        tooltip.classList.add(ClassName("diagram-tooltip"))
        tooltip.style.pointerEvents = "none"

        val background: SVGPolygonElement = document.createElementNS("http://www.w3.org/2000/svg", "polygon") as SVGPolygonElement
        background.style.fill = "#00000090"
        tooltip.appendChild(background)

        val legend: SVGGElement = document.createElementNS("http://www.w3.org/2000/svg", "g") as SVGGElement
        legend.innerHTML = """
            <rect x="0.0" y="0.0" width="${40 * fontSizePercentage}" height="${13 * fontSizePercentage}" style="fill:${keyColor};"></rect>
            <text x="${45 * fontSizePercentage}" y="${11 * fontSizePercentage}" text-rendering="optimizeLegibility" style="fill: white; font-size: ${(13 * fontSizePercentage).toInt()}px; font-family: sans-serif; pointer-events: none;">$keyLabel</text>
        """.trimIndent()
        tooltip.appendChild(legend)

        val imageY: Double
        if (keyDescription.isNotBlank()) {
            val description: SVGTextElement = document.createElementNS("http://www.w3.org/2000/svg", "text") as SVGTextElement
            description.setAttribute("text-rendering", "optimizeLegibility")
            description.setAttribute("style", "font-size: ${(13 * fontSizePercentage).toInt()}px; font-family: sans-serif; fill: white")
            description.innerHTML = keyDescription
            description.setAttribute("transform", "translate(0,${30 * fontSizePercentage})")
            tooltip.appendChild(description)

            imageY = 35.0 * fontSizePercentage
        } else {
            imageY = 20.0 * fontSizePercentage
        }
        if (keyImageHref.isNotBlank()) {
            val image: SVGImageElement = document.createElementNS("http://www.w3.org/2000/svg", "image") as SVGImageElement
            image.setAttribute("href", keyImageHref)
            image.setAttribute("height", "60")
            image.setAttribute("transform", "translate(0,${imageY})")
            tooltip.appendChild(image)
        }

        g.onmouseenter = EventHandler { e: MouseEvent ->
            showTooltip(e)
        }
        g.onmouseleave = EventHandler {
            if (parent.s.contains(tooltip)) {
                tooltip.remove()
            }
        }
    }

    fun showTooltip(e: MouseEvent) {
        parent.s.appendChild(tooltip)
        val margin = 10 * parent.getFontSizePercentage()
        val background = tooltip.querySelector("polygon")!! as SVGPolygonElement
        if (background.getAttribute("points") == null) {
            val contentWidth = tooltip.getBBox().width
            val contentHeight = tooltip.getBBox().height
            background.setAttribute("points",
                "${-contentWidth / 2 - margin * 2},0 " +
                        "${-contentWidth / 2 - margin},${-margin} " +
                        "${contentWidth / 2 + margin},${-margin} " +
                        "${contentWidth / 2 + margin},${contentHeight + margin * 0.3} " +
                        "${-contentWidth / 2 - margin},${contentHeight + margin * 0.3} " +
                        "${-contentWidth / 2 - margin},${margin}")
        }
        val diagramScrollX = if (xScrolled) (parent.transformArea?.g?.getAttribute("scroll-x")?.toDouble() ?: 0.0) else 0.0
        val diagramScrollY = if (yScrolled) (parent.transformArea?.g?.getAttribute("scroll-y")?.toDouble() ?: 0.0) else 0.0
        val diagramMinX = parent.s.viewBox.baseVal.x
        val diagramMaxX = diagramMinX + parent.s.viewBox.baseVal.width
        val mouseX = parent.translateX(e.clientX.toDouble())
        val bBox: DOMRect = g.getBBox()
        if (bBox.x + bBox.width + background.getBBox().width + diagramScrollX < diagramMaxX) {
            // shape right
            background.setAttribute("transform", "translate(${(background.getBBox().width - margin * 3) / 2},0)")
            tooltip.setAttribute("transform", "translate(${bBox.x + bBox.width + margin * 2 + diagramScrollX},${bBox.y + diagramScrollY})")
        } else if (bBox.x - background.getBBox().width + diagramScrollX > diagramMinX) {
            // shape left
            background.setAttribute("transform", "scale(-1,1) translate(${-(background.getBBox().width - margin * 3) / 2},0)")
            tooltip.setAttribute("transform", "translate(${bBox.x - (tooltip.getBBox().width - margin) + diagramScrollX},${bBox.y + diagramScrollY})")
        } else if (mouseX + margin * 2 + background.getBBox().width < diagramMaxX) {
            // mouse right (But keep margin*2 away)
            background.setAttribute("transform", "translate(${(background.getBBox().width - margin * 3) / 2},0)")
            tooltip.setAttribute("transform", "translate(${mouseX + margin * 2 + margin * 2},${bBox.y + diagramScrollY})")
        } else {
            // mouse left (But keep margin*2 away)
            background.setAttribute("transform", "scale(-1,1) translate(${-(background.getBBox().width - margin * 3) / 2},0)")
            tooltip.setAttribute("transform", "translate(${mouseX - margin * 2 - (tooltip.getBBox().width - margin)},${bBox.y + diagramScrollY})")
        }
    }
}