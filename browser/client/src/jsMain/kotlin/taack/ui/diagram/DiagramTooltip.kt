package taack.ui.diagram

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.events.EventHandler
import web.geometry.DOMRect
import web.html.HTMLDivElement
import web.html.HTMLImageElement
import web.http.GET
import web.http.RequestMethod
import web.location.location
import web.svg.*
import web.timers.Interval
import web.timers.clearInterval
import web.timers.setInterval
import web.uievents.MouseEvent
import web.url.URL
import web.xhr.XMLHttpRequest
import kotlin.math.min

class DiagramTooltip(private val parent: Diagram, val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramTooltip(d: Diagram): List<DiagramTooltip> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='TOOLTIP']").asList()
            return elements.map {
                DiagramTooltip(d, it as SVGGElement)
            }
        }
    }

    private val keyLabel: String = g.attributes.getNamedItem("key-label")?.value ?: ""
    private val keyColor: String = g.attributes.getNamedItem("key-color")?.value ?: ""
    private val keyDescription: String = g.attributes.getNamedItem("key-description")?.value ?: ""
    private val keyImageHref: String = g.attributes.getNamedItem("key-image-href")?.value ?: ""
    private val xScrolled: Boolean = g.attributes.getNamedItem("x-scrolled")?.value?.toBoolean() ?: true
    private val yScrolled: Boolean = g.attributes.getNamedItem("y-scrolled")?.value?.toBoolean() ?: true
    private val tooltip: SVGGElement?

    private val diagramActionUrl: String = g.attributes.getNamedItem("diagram-action-url")?.value ?: ""
    private val dataX = g.attributes.getNamedItem("data-x")?.value ?: ""
    private val dataY = g.attributes.getNamedItem("data-y")?.value ?: ""

    init {
        g.querySelectorAll("text").forEach { (it as SVGTextElement).style.pointerEvents = "unset" }

        if (keyLabel.isNotBlank()) {
            val fontSizePercentage = parent.getFontSizePercentage()
            tooltip = document.createElementNS("http://www.w3.org/2000/svg", "g") as SVGGElement
            tooltip.classList.add(ClassName("diagram-tooltip"))
            tooltip.style.pointerEvents = "none"

            val background: SVGPolygonElement = document.createElementNS("http://www.w3.org/2000/svg", "polygon") as SVGPolygonElement
            background.classList.add(ClassName("diagram-tooltip-background"))
            tooltip.appendChild(background)

            val legend: SVGGElement = document.createElementNS("http://www.w3.org/2000/svg", "g") as SVGGElement
            legend.innerHTML = """
            <rect x="0.0" y="0.0" width="${40 * fontSizePercentage}" height="${13 * fontSizePercentage}" ${if (keyColor.isNotBlank()) "style='fill: ${keyColor};'" else "class='diagram-tooltip-legend'"}></rect>
            <text x="${45 * fontSizePercentage}" y="${11 * fontSizePercentage}" text-rendering="optimizeLegibility" style="font-size: ${(13 * fontSizePercentage).toInt()}px; font-family: sans-serif; pointer-events: none;">$keyLabel</text>
        """.trimIndent()
            tooltip.appendChild(legend)

            val maxWidth: Double = parent.s.viewBox.baseVal.width.let { if (it > 0) it else parent.s.width.baseVal.value.toDouble() } / 2
            if (keyDescription.isNotBlank()) {
                val testHTML : HTMLDivElement = document.createElement("div") as HTMLDivElement
                testHTML.innerHTML = keyDescription
                if (testHTML.textContent?.trim() == keyDescription.trim()) {
                    val description: SVGTextElement = document.createElementNS("http://www.w3.org/2000/svg", "text") as SVGTextElement
                    description.setAttribute("text-rendering", "optimizeLegibility")
                    description.setAttribute("style", "font-size: ${(13 * fontSizePercentage).toInt()}px; font-family: sans-serif;")
                    description.innerHTML = keyDescription
                    description.setAttribute("transform", "translate(0,${30 * fontSizePercentage})")
                    tooltip.appendChild(description)
                } else {
                    val description: SVGForeignObjectElement = document.createElementNS("http://www.w3.org/2000/svg", "foreignObject") as SVGForeignObjectElement
                    description.setAttribute("transform", "translate(0,${20 * fontSizePercentage})")
                    description.setAttribute("width", "100%")
                    description.setAttribute("height", "100%")
                    val descriptionContainer: HTMLDivElement = document.createElement("div") as HTMLDivElement
                    descriptionContainer.classList.add(ClassName("diagram-tooltip-description"))
                    descriptionContainer.innerHTML = keyDescription
                    description.appendChild(descriptionContainer)

                    parent.s.appendChild(description)
                    val descriptionWidth = min(descriptionContainer.clientWidth.toDouble(), maxWidth)
                    description.setAttribute("width", descriptionWidth.toString())
                    val descriptionHeight = descriptionContainer.clientHeight
                    description.setAttribute("height", descriptionHeight.toString())
                    description.remove()
                    tooltip.appendChild(description)
                }
            }


            g.onmouseenter = EventHandler { e: MouseEvent ->
                showTooltip(e)
            }
            g.onmouseleave = EventHandler {
                if (parent.s.contains(tooltip)) {
                    tooltip.remove()
                }
            }
        } else {
            tooltip = null
        }

        if (diagramActionUrl.isNotBlank()) {
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
                    onClickTooltip(diagramActionUrl, keyLabel, dataX, dataY)
                    isClicked = false
                }
            }
        }
    }

    fun showTooltip(e: MouseEvent, refreshBackground: Boolean = false) {
        if (tooltip != null) {
            parent.s.appendChild(tooltip)

            val fontSizePercentage = parent.getFontSizePercentage()
            val margin = 10 * fontSizePercentage
            if (keyImageHref.isNotBlank() && tooltip.querySelector(".diagram-tooltip-images") == null) { // todo: show current 1 month by default.
                val images: SVGForeignObjectElement = document.createElementNS("http://www.w3.org/2000/svg", "foreignObject") as SVGForeignObjectElement
                images.style.display = "none"
                images.setAttribute("transform", "translate(${tooltip.getBBox().width},0)")
                images.setAttribute("width", "100%")
                images.setAttribute("height", "100%")
                val imagesContainer: HTMLDivElement = document.createElement("div") as HTMLDivElement
                imagesContainer.classList.add(ClassName("diagram-tooltip-images"))
                for (imageHref in keyImageHref.split(",")) {
                    val image = document.createElement("img") as HTMLImageElement
                    image.setAttribute("src", imageHref)
                    image.setAttribute("width", (60 * fontSizePercentage).toString())
                    image.style.marginLeft = "${margin}px"
                    image.style.marginBottom = "${margin / 2}px"
                    imagesContainer.appendChild(image)
                }
                images.appendChild(imagesContainer)
                tooltip.appendChild(images)

                var poll: Interval? = null
                poll = setInterval({
                    if (imagesContainer.getElementsByTagName("img").asList().none { (it as HTMLImageElement).height == 0 }) { // should wait the images rendered
                        clearInterval(poll)
                        images.style.display = ""
                        images.setAttribute("width", imagesContainer.clientWidth.toString())
                        images.setAttribute("height", imagesContainer.clientHeight.toString())
                        showTooltip(e, true)
                    }
                }, 10)
            }

            val background = tooltip.querySelector(".diagram-tooltip-background")!! as SVGPolygonElement
            if (refreshBackground) {
                background.removeAttribute("points")
            }
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
            } else if (mouseX - margin * 2 - background.getBBox().width > diagramMinX) {
                // mouse left (But keep margin*2 away)
                background.setAttribute("transform", "scale(-1,1) translate(${-(background.getBBox().width - margin * 3) / 2},0)")
                tooltip.setAttribute("transform", "translate(${mouseX - margin * 2 - (tooltip.getBBox().width - margin)},${bBox.y + diagramScrollY})")
            } else {
                // shape right
                background.setAttribute("transform", "translate(${(background.getBBox().width - margin * 3) / 2},0)")
                tooltip.setAttribute("transform", "translate(${bBox.x + bBox.width + margin * 2 + diagramScrollX},${bBox.y + diagramScrollY})")
            }
        }
    }

    private fun onClickTooltip(action: String, key: String, x: String, y: String) {
        val targetUrl = URL(action + (if (action.contains("?")) "&" else "?") + "key=${key}&x=${x}&y=${y}&isAjax=true", "${location.protocol}//${location.host}").toString()

        //Display load spinner
        val loader = document.getElementById(ElementId("taack-load-spinner"))
        loader?.classList?.remove(ClassName("tck-hidden"))
        val xhr = XMLHttpRequest()

        xhr.onloadend = EventHandler { ev ->
            checkLogin(xhr)
            ev.preventDefault()
            trace("DiagramData::onClickShape: Load End, action: $action responseType: '${xhr.responseType}'")
            loader?.classList?.add(ClassName("tck-hidden"))

            val text = xhr.responseText
            if (text.substring(0, min(20, text.length)).contains(Regex(" html"))) {
                trace("Full webpage ...|$action|${document.title}|${document.documentURI}")
                window.open(targetUrl, "_blank")
//                history.pushState("{}", document.title, targetUrl)
//                trace("Setting location.href: $targetUrl")
//                location.href = targetUrl
//                document.textContent = text
//                document.close()
            } else {
                trace("BaseAjaxAction::onclickBaseAjaxAction => processAjaxLink $parent")
                processAjaxLink(null, text, parent)
            }
        }
        xhr.open(RequestMethod.GET, targetUrl)
        xhr.send()
    }
}