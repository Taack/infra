package taack.ui.diagram

import js.array.asList
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.events.EventHandler
import web.history.history
import web.http.GET
import web.http.RequestMethod
import web.location.location
import web.svg.*
import web.url.URL
import web.xhr.XMLHttpRequest
import kotlin.math.min

class DiagramData(private val parent: DiagramTransformArea, val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramData(dataGroup: DiagramTransformArea): List<DiagramData> {
            val elements: List<*> = dataGroup.g.querySelectorAll("g[element-type='DATA']").asList()
            return elements.map {
                DiagramData(dataGroup, it as SVGGElement)
            }
        }
    }

    val dataset: String = g.attributes.getNamedItem("dataset")!!.value
    val gapIndex = g.attributes.getNamedItem("gap-index")?.value?.toInt()
    private val shapes: List<SVGElement> = g.children.asList().filter { it.tagName != "text" }.unsafeCast<List<SVGElement>>()
    private val tooltip: SVGGElement?

    init {
        val tooltipLabel = g.getAttribute("data-label")
        if (!tooltipLabel.isNullOrBlank()) {
            val diagramRoot = parent.parent
            tooltip = document.createElement("g") as SVGGElement
            tooltip.classList.add(ClassName("diagram-tooltip"))

            val background: SVGPolygonElement = document.createElement("polygon") as SVGPolygonElement
            background.style.fill = "#00000090"
            tooltip.appendChild(background)

            val legend: SVGGElement = diagramRoot.cloneLegendShape(dataset)
            legend.querySelectorAll("text").forEach { (it as SVGTextElement).style.fill = "white" }
            legend.setAttribute("transform", "translate(0,-15)")
            tooltip.appendChild(legend)

            val value: SVGTextElement = document.createElement("text") as SVGTextElement
            value.setAttribute("text-rendering", "optimizeLegibility")
            value.setAttribute("style", "font-size: 13px; font-family: sans-serif; fill: white")
            value.innerHTML = tooltipLabel
            value.setAttribute("transform", "translate(0,15)")
            tooltip.appendChild(value)

            if (parent.currentHoverLine == null) {
                g.onmouseenter = EventHandler {
                    showTooltip()
                }
                g.onmouseleave = EventHandler {
                    if (diagramRoot.s.contains(tooltip)) {
                        tooltip.remove()
                    }
                }
            }
        } else {
            tooltip = null
        }

        val action = parent.g.getAttribute("diagram-action-url")
        val dataX = g.getAttribute("data-x")
        val dataY = g.getAttribute("data-y")
        if (!action.isNullOrBlank() && (dataX != null || dataY != null)) {
            g.style.cursor = "pointer"

            var isClicked = false
            g.onmousedown = EventHandler {
                isClicked = true
            }
            g.onmousemove = EventHandler { // avoid conflict with Scroll
                isClicked = false
            }
            g.onmouseup = EventHandler {
                if (isClicked) {
                    onClickShape(action, dataset, dataX ?: "", dataY ?: "")
                    isClicked = false
                }
            }
        }
    }

    fun hideOrShow(toShow: Boolean) {
        g.style.display = if (toShow) "" else "none"
    }

    fun moveShapeHorizontally(startX: Double, shapeWidth: Double) {
        shapes.forEach { shape ->
            when (shape.tagName) {
                "rect" -> {
                    shape.setAttribute("x", startX.toString())
                    shape.setAttribute("width", shapeWidth.toString())
                }
                "circle" -> {
                    shape.setAttribute("cx", startX.toString())
                }
                "line" -> {
                    val x1 = shape.getAttribute("x1")
                    val x2 = shape.getAttribute("x2")
                    if (x1 == x2) { // vertical line
                        shape.setAttribute("x1", (startX + shapeWidth / 2).toString()) // for whiskers: move to center point
                        shape.setAttribute("x2", (startX + shapeWidth / 2).toString())
                    } else {
                        shape.setAttribute("x1", startX.toString())
                        shape.setAttribute("x2", (startX + shapeWidth).toString())
                    }
                }
            }
        }
    }

    fun moveShapeVertically(startY: Double, shapeHeight: Double? = null): Double { // return startY of next shape which should be stacked
        var y: Double = startY
        var height: Double? = shapeHeight
        shapes.forEach { shape ->
            when (shape.tagName) {
                "rect" -> {
                    if (height != null) {
                        shape.setAttribute("height", height.toString())
                    }
                    y = startY - shape.getAttribute("height")!!.toDouble()
                    shape.setAttribute("y", y.toString())
                }
                "circle" -> {
                    shape.setAttribute("cy", startY.toString())
                }
                "line" -> {
                    val y1 = shape.getAttribute("y1")
                    val y2 = shape.getAttribute("y2")
                    if (y1 != y2) {
                        if (height == null) {
                            height = shape.getAttribute("y1")!!.toDouble() - shape.getAttribute("y2")!!.toDouble()
                        }
                        shape.setAttribute("y1", startY.toString())
                        shape.setAttribute("y2", (startY - height!!).toString())
                    } else { // horizontal line
                        shape.setAttribute("y1", (startY - (height ?: 0.0)).toString()) // for whiskers: move to top point
                        shape.setAttribute("y2", (startY - (height ?: 0.0)).toString())
                    }
                    y = shape.getAttribute("y2")!!.toDouble()
                }
            }
        }
        return y
    }

    fun getShapeAttribute(name: String): String? {
        return shapes.firstOrNull()?.getAttribute(name)
    }

    private fun onClickShape(action: String, dataset: String, x: String, y: String) {
        val targetUrl = URL(action + (if (action.contains("?")) "&" else "?") + "dataset=${dataset}&x=${x}&y=${y}&isAjax=true", "${location.protocol}//${location.host}").toString()

        //Display load spinner
        val loader = document.getElementById(ElementId("taack-load-spinner"))
        loader?.classList?.remove(ClassName("tck-hidden"))
        val xhr = XMLHttpRequest()

        xhr.onloadend = EventHandler { ev ->
            ev.preventDefault()
            trace("DiagramData::onClickShape: Load End, action: $action responseType: '${xhr.responseType}'")
            loader?.classList?.add(ClassName("tck-hidden"))

            val text = xhr.responseText
            if (text.substring(0, min(20, text.length)).contains(Regex(" html"))) {
                trace("Full webpage ...|$action|${document.title}|${document.documentURI}")
                history.pushState("{}", document.title, targetUrl)
                trace("Setting location.href: $targetUrl")
                location.href = targetUrl
                document.textContent = text
                document.close()
            } else {
                trace("BaseAjaxAction::onclickBaseAjaxAction => processAjaxLink $parent")
                processAjaxLink(null, text, parent)
            }
        }
        xhr.open(RequestMethod.GET, targetUrl)
        xhr.send()
    }

    fun showTooltip() { // todo: bad show at high resolution
        if (tooltip != null) {
            val diagramRoot = parent.parent
            diagramRoot.s.appendChild(tooltip)

            val background = tooltip.querySelector("polygon")!! as SVGPolygonElement
            if (background.getAttribute("points") == null) {
                val contentWidth = tooltip.getBBox().width
                background.setAttribute("points", "${-contentWidth / 2 - 20},0 ${-contentWidth / 2 - 10},10 ${-contentWidth / 2 - 10},25 ${contentWidth / 2 + 10},25 ${contentWidth / 2 + 10},-25 ${-contentWidth / 2 - 10},-25 ${-contentWidth / 2 - 10},-10")
            }

            val diagramScrollX = diagramRoot.transformArea?.g?.getAttribute("scroll-x")?.toDouble() ?: 0.0
            if (g.getBBox().x + g.getBBox().width + background.getBBox().width + diagramScrollX < diagramRoot.s.viewBox.baseVal.x + diagramRoot.s.viewBox.baseVal.width) {
                background.setAttribute("transform", "translate(${(background.getBBox().width - 30) / 2},0)")
                tooltip.setAttribute("transform", "translate(${g.getBBox().x + g.getBBox().width + 20 + diagramScrollX},${g.getBBox().y + (if (shapes.firstOrNull()?.tagName == "circle") g.getBBox().height / 2.0 else 0.0)})")
            } else {
                background.setAttribute("transform", "scale(-1,1) translate(${-(background.getBBox().width - 30) / 2},0)")
                tooltip.setAttribute("transform", "translate(${g.getBBox().x - (tooltip.getBBox().width - 10) + diagramScrollX},${g.getBBox().y + (if (shapes.firstOrNull()?.tagName == "circle") g.getBBox().height / 2.0 else 0.0)})")
            }
        }
    }
}