package taack.ui.diagram

import js.array.asList
import web.svg.SVGGElement
import web.svg.SVGLineElement
import web.svg.SVGTextElement
import web.uievents.MouseEvent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class DiagramTransformArea(val parent: Diagram, val g: SVGGElement) {
    companion object {
        fun getSiblingDiagramTransformArea(d: Diagram): DiagramTransformArea? {
            val g = d.s.querySelector("g[element-type='TRANSFORM_AREA']")
            return if (g != null) DiagramTransformArea(d, g as SVGGElement) else null
        }
    }

    private val dataList: List<DiagramData> = DiagramData.getSiblingDiagramData(this)
    private val shapeType: String = g.attributes.getNamedItem("shape-type")!!.value
    private val maxShapeWidth: Double = g.attributes.getNamedItem("shape-max-width")!!.value.toDouble()
    private val areaMinX: Double = g.attributes.getNamedItem("area-min-x")!!.value.toDouble()
    private val areaMaxX: Double = g.attributes.getNamedItem("area-max-x")!!.value.toDouble()
    private val areaMaxY: Double = g.attributes.getNamedItem("area-max-y")!!.value.toDouble()

    private val backgroundVerticalLines = g.querySelectorAll("g[element-type='VERTICAL_BACKGROUND']>line").asList()
    private val backgroundXLabels = g.querySelectorAll("g[element-type='VERTICAL_BACKGROUND']>text").asList()
    private var gapWidth: Double = (areaMaxX - areaMinX) / (backgroundVerticalLines.size - 1)

    fun hideOrShowDataset(toShow: Boolean, dataset: String) {
        dataList.filter { it.dataset == dataset }.forEach {
            it.hideOrShow(toShow)
        }
        refreshDataShape()
    }

    fun scrollBy(movingDistance: Double) {
        scrollTo((g.getAttribute("scroll-x")?.toDouble() ?: 0.0) + movingDistance)
    }

    private fun scrollTo(x: Double) {
        if (backgroundVerticalLines.isNotEmpty()) {
            val minX = areaMaxX - round(backgroundVerticalLines.last().getAttribute("x1")!!.toDouble())
            val maxX = areaMinX - round(backgroundVerticalLines.first().getAttribute("x1")!!.toDouble())
            val adjustedX = min(maxX, max(minX, x))
            g.setAttribute("scroll-x", adjustedX.toString())
            g.setAttribute("transform", "translate(${adjustedX},0.0)")
        }
    }

    fun zoom(isUp: Boolean) {
        if (backgroundVerticalLines.isNotEmpty()) {
            val currentMinLineIndex = backgroundVerticalLines.indexOf(backgroundVerticalLines.find { line -> round(parent.translateX(line.getBoundingClientRect().x)) >= areaMinX } ?: backgroundVerticalLines.first())
            val currentMaxLineIndex = backgroundVerticalLines.indexOf(backgroundVerticalLines.findLast { line -> round(parent.translateX(line.getBoundingClientRect().x)) <= areaMaxX } ?: backgroundVerticalLines.last())
            val changeGap = max(1, (currentMaxLineIndex - currentMinLineIndex) / 10)
            val newMinLineIndex = max(0, if (isUp) currentMinLineIndex + changeGap else currentMinLineIndex - changeGap)  // will move this line as new first background vertical line
            val newMaxLineIndex = min(backgroundVerticalLines.size - 1, if (isUp) currentMaxLineIndex - changeGap else currentMaxLineIndex + changeGap) // will move this line as new last background vertical line
            if (newMaxLineIndex - newMinLineIndex > 1 && (currentMinLineIndex != newMinLineIndex || currentMaxLineIndex != newMaxLineIndex)) {
                val newMinLine = backgroundVerticalLines[newMinLineIndex] as SVGLineElement
                val newMaxLine = backgroundVerticalLines[newMaxLineIndex] as SVGLineElement
                val zoomRadio = (areaMaxX - areaMinX) / (newMaxLine.x1.baseVal.value - newMinLine.x1.baseVal.value)

                backgroundVerticalLines.forEach { line ->
                    val targetX = ((line.getAttribute("x1")?.toDouble() ?: areaMinX) - areaMinX) * zoomRadio + areaMinX
                    line.setAttribute("x1", targetX.toString())
                    line.setAttribute("x2", targetX.toString())
                }
                backgroundXLabels.forEach { text ->
                    if (text.hasAttribute("rotated-label-offset-x")) {
                        val offset = text.getAttribute("rotated-label-offset-x")!!.toDouble()

                        val targetX = ((text.getAttribute("x")?.toDouble() ?: (areaMinX - offset)) + offset - areaMinX) * zoomRadio + areaMinX - offset
                        text.setAttribute("x", targetX.toString())

                        val s = text.getAttribute("transform")!!.split(",").toMutableList()
                        s[1] = (targetX + offset).toString()
                        text.setAttribute("transform", s.joinToString(","))
                    } else {
                        val width = (text as SVGTextElement).getBBox().width
                        val targetX = ((text.getAttribute("x")?.toDouble() ?: (areaMinX - width / 2)) + width / 2 - areaMinX) * zoomRadio + areaMinX - width / 2
                        text.setAttribute("x", targetX.toString())
                    }
                }
                refreshDataShape(zoomRadio)
                scrollTo(areaMinX - newMinLine.x1.baseVal.value)
            }
        }
    }

    private fun refreshDataShape(zoomRadio: Double = 1.0) {
        gapWidth *= zoomRadio
        when (shapeType) {
            in listOf("bar", "whiskers") -> {
                dataList.filter { it.gapIndex != null }.groupBy { it.gapIndex }.values.forEach { gapDataList ->
                    val displayedDataset: Map<String, List<DiagramData>> = gapDataList.filter { it.g.style.display != "none" }.groupBy { it.dataset }
                    if (displayedDataset.isNotEmpty()) {
                        val shapeNumber = displayedDataset.size
                        var gapHorizontalPadding: Double = gapWidth * 0.2 / 2
                        var shapeWidth: Double = if (shapeNumber > 1) (gapWidth * 0.8) * 0.8 / shapeNumber else gapWidth * 0.8
                        val shapeMargin: Double = if (shapeNumber > 1) (gapWidth * 0.8) * 0.2 / (shapeNumber - 1) else 0.0
                        if (maxShapeWidth > 0 && shapeWidth > maxShapeWidth) {
                            shapeWidth = maxShapeWidth
                            gapHorizontalPadding = (gapWidth - shapeWidth * shapeNumber - shapeMargin * (shapeNumber - 1)) / 2
                        }
                        for (key in displayedDataset.keys) {
                            val index = displayedDataset.keys.indexOf(key)
                            displayedDataset[key]!!.forEach {
                                it.moveShapeHorizontally(areaMinX + gapWidth * it.gapIndex!! + gapHorizontalPadding + (shapeWidth + shapeMargin) * index, shapeWidth)
                            }
                        }
                    }
                }
            }
            "stackedBar" -> {
                if (zoomRadio != 1.0) {
                    var gapHorizontalPadding: Double = gapWidth * 0.2 / 2
                    var shapeWidth: Double = gapWidth * 0.8
                    if (maxShapeWidth > 0 && shapeWidth > maxShapeWidth) {
                        shapeWidth = maxShapeWidth
                        gapHorizontalPadding = (gapWidth - shapeWidth) / 2
                    }
                    dataList.filter { it.g.style.display != "none" }.forEach {
                        it.moveShapeHorizontally(areaMinX + gapWidth * it.gapIndex!! + gapHorizontalPadding, shapeWidth)
                    }
                } else {
                    dataList.filter { it.gapIndex != null }.groupBy { it.gapIndex }.values.forEach { gapDataList ->
                        val displayedDataset: Map<String, List<DiagramData>> = gapDataList.filter { it.g.style.display != "none" }.groupBy { it.dataset }
                        if (displayedDataset.isNotEmpty()) {
                            var startY: Double = areaMaxY
                            for (key in displayedDataset.keys) {
                                displayedDataset[key]!!.forEach {
                                    startY = it.moveShapeVertically(startY)
                                }
                            }
                        }
                    }
                }
            }
            "scatter" -> {
                if (zoomRadio != 1.0) {
                    dataList.forEach {
                        val targetX = ((it.getShapeAttribute("cx")?.toDouble() ?: areaMinX) - areaMinX) * zoomRadio + areaMinX
                        it.moveShapeHorizontally(targetX, 0.0)
                    }
                }
            }
            "line" -> {
                if (zoomRadio != 1.0) {
                    dataList.forEach {
                        val cx = it.getShapeAttribute("cx")?.toDouble()
                        if (cx != null) { // circle
                            it.moveShapeHorizontally((cx - areaMinX) * zoomRadio + areaMinX, 0.0)
                        } else {
                            val x1 = it.getShapeAttribute("x1")?.toDouble()
                            val x2 = it.getShapeAttribute("x2")?.toDouble()
                            if (x1 != null && x2 != null) { // line between circles
                                val targetX1 = (x1 - areaMinX) * zoomRadio + areaMinX
                                val targetX2 = (x2 - areaMinX) * zoomRadio + areaMinX
                                it.moveShapeHorizontally(targetX1, targetX2 - targetX1)
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }

    fun isClientMouseInTransformArea(e: MouseEvent): Boolean {
        return parent.translateX(e.clientX.toDouble()) in areaMinX..areaMaxX
    }
}