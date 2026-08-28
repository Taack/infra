package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.svg.*
import kotlin.math.*
import kotlinx.browser.window

class DiagramVerticalBackground(private val parent: Diagram, private val g: SVGGElement): LeafElement {
    companion object {
        fun getSiblingDiagramVerticalBackground(d: Diagram): DiagramVerticalBackground? {
            val g = d.s.querySelector("g[element-type='VERTICAL_BACKGROUND']")
            return if (g != null) DiagramVerticalBackground(d, g as SVGGElement) else null
        }
    }

    val areaMinX: Double = g.attributes.getNamedItem("area-min-x")!!.value.toDouble()
    val areaMaxX: Double = g.attributes.getNamedItem("area-max-x")!!.value.toDouble()
    private val verticalBackgroundLines = g.querySelectorAll("line")?.asList() ?: listOf()
    private val verticalBackgroundTexts = g.querySelectorAll("text")?.asList() ?: listOf()
    private val verticalBackgroundTodayLine = g.querySelector("rect")
    var gapWidth: Double = if (verticalBackgroundLines.size > 1)
        verticalBackgroundLines[1].getAttribute("x1")!!.toDouble() - verticalBackgroundLines[0].getAttribute("x1")!!.toDouble()
    else (areaMaxX - areaMinX)

    private val defaultScrollXNumber: Int = g.attributes.getNamedItem("default-scroll-x-number")?.value?.toIntOrNull() ?: 0

    init {
        if (defaultScrollXNumber != 0 && verticalBackgroundLines.isNotEmpty()) {
            horizontalZoom(max(1.0, verticalBackgroundLines.size.toDouble() / defaultScrollXNumber.toDouble()), if (defaultScrollXNumber > 0) areaMaxX else 0.0) // show last/first N vertical lines
        } else if ((g.getAttribute("show-label-every-x")?.toDouble() ?: 1.0) > 1) {
            refreshBackgroundXLabelsDisplay()
        }
    }

    fun horizontalScrollBy(movingDistance: Double) {
        horizontalScrollTo(parent.scrollX + movingDistance)
    }

    private fun horizontalScrollTo(x: Double) {
        if (verticalBackgroundLines.isNotEmpty()) {
            val margin = 50.0 * parent.getFontSizePercentage()
            val minX = areaMaxX - (round(verticalBackgroundLines.last().getAttribute("x1")!!.toDouble()) + margin)
            val maxX = margin
            val adjustedX = min(maxX, max(minX, x))
            g.setAttribute("transform", "translate(${adjustedX},0.0)")
            parent.dataContainers.forEach { dataContainer ->
                dataContainer.g.setAttribute("transform", "translate(${adjustedX},${parent.scrollY})")
            }
            parent.scrollX = adjustedX
        }
    }

    private var zoomTimer: Int? = null
    private fun refreshBackgroundXLabelsDisplay(zoomRadio: Double = 1.0) {
        val showLabelEveryX = (g.getAttribute("show-label-every-x")?.toDouble() ?: 1.0) / zoomRadio
        g.setAttribute("show-label-every-x", showLabelEveryX.toString())

        // refresh the display only after stopping Zoom
        if (zoomTimer != null) {
            window.clearTimeout(zoomTimer!!)
        }
        zoomTimer = window.setTimeout({
            verticalBackgroundTexts.forEachIndexed { index, text ->
                (text as SVGTextElement).style.display = if (index % ceil(round(showLabelEveryX * 100) / 100).toInt() == 0 || text.previousElementSibling == verticalBackgroundTodayLine) "" else "none"
            }
        }, if (zoomRadio == 1.0) 0 else 500)

        // The lines of Timeline diagram are always in DAY unit, and will be hidden/displayed when Zoom (Sames as Texts)
        if (parent.dataContainers.firstOrNull()?.getShapeType() == "timeline") {
            verticalBackgroundLines.forEachIndexed { index, line ->
                (line as SVGLineElement).style.display = if (index % ceil(round(showLabelEveryX * 100) / 100).toInt() == 0) "" else "none"
            }
        }
    }

    private fun refreshBackgroundXLabelsPosition(zoomRadio: Double) {
        verticalBackgroundTexts.forEach { text ->
            val labelWidth = text.getAttribute("label-width")?.toDouble() ?: 0.0
            if (text.getAttribute("transform")?.startsWith("rotate") == true) {
                val targetX = ((text.getAttribute("x")?.toDouble() ?: (areaMinX - labelWidth)) + labelWidth - areaMinX) * zoomRadio + areaMinX - labelWidth
                text.setAttribute("x", targetX.toString())

                val s = text.getAttribute("transform")!!.split(",").toMutableList()
                s[1] = (targetX + labelWidth).toString()
                text.setAttribute("transform", s.joinToString(","))
            } else {
                val targetX = ((text.getAttribute("x")?.toDouble() ?: (areaMinX - labelWidth / 2)) + labelWidth / 2 - areaMinX) * zoomRadio + areaMinX - labelWidth / 2
                text.setAttribute("x", targetX.toString())
            }
        }
    }

    fun horizontalZoom(zoomRadio: Double, mouseX: Double) {
        if (verticalBackgroundLines.isNotEmpty()) {
            val adjustedZoomRadio = if (zoomRadio > 1 && (areaMaxX - areaMinX) / (gapWidth * 2) > 1) {
                min(zoomRadio, (areaMaxX - areaMinX) / gapWidth / 2)
            } else if (zoomRadio < 1 && verticalBackgroundLines.last().getAttribute("x1")!!.toDouble().toInt() > areaMaxX) {
                max(zoomRadio, (areaMaxX - areaMinX) / (verticalBackgroundLines.last().getAttribute("x1")!!.toDouble() - areaMinX))
            } else {
                1.0
            }
            if (adjustedZoomRadio != 1.0) {
                verticalBackgroundLines.forEach { line ->
                    val targetX = ((line.getAttribute("x1")?.toDouble() ?: areaMinX) - areaMinX) * adjustedZoomRadio + areaMinX
                    line.setAttribute("x1", targetX.toString())
                    line.setAttribute("x2", targetX.toString())
                }
                if (verticalBackgroundTodayLine != null) {
                    val targetX = ((verticalBackgroundTodayLine.getAttribute("x")?.toDouble() ?: areaMinX) - areaMinX) * adjustedZoomRadio + areaMinX
                    verticalBackgroundTodayLine.setAttribute("x", targetX.toString())
                }
                refreshBackgroundXLabelsPosition(adjustedZoomRadio)
                refreshBackgroundXLabelsDisplay(adjustedZoomRadio)
                gapWidth *= adjustedZoomRadio
                parent.dataContainers.forEach { dataContainer ->
                    dataContainer.refreshDataShape(adjustedZoomRadio)
                }
            }
            if (zoomRadio < 1 && adjustedZoomRadio != zoomRadio) { // reset the scroll when zoom-down to default preview
                horizontalScrollTo(0.0)
            } else if (adjustedZoomRadio != 1.0) { // auto scroll so that it seems zooming to the mouse position
                horizontalScrollBy((mouseX - parent.scrollX - areaMinX) * (1 - adjustedZoomRadio))
            }
        }
    }
}