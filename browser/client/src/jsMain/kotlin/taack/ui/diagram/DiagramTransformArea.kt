package taack.ui.diagram

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.BaseElement
import taack.ui.base.element.Block
import web.dom.document
import web.svg.*
import kotlin.math.*

class DiagramTransformArea(val parent: Diagram, val g: SVGGElement): BaseElement {
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

    private val verticalBackground = g.querySelector("g[element-type='VERTICAL_BACKGROUND']")
    private val verticalBackgroundLines = verticalBackground?.querySelectorAll("line")?.asList() ?: listOf()
    private val verticalBackgroundTexts = verticalBackground?.querySelectorAll("text")?.asList() ?: listOf()

    private var gapWidth: Double = (areaMaxX - areaMinX) / (verticalBackgroundLines.size - 1)
    var currentHoverLine: SVGLineElement? = null

    init {
        if (shapeType == "line" && dataList.any { it.g.hasAttribute("data-label") }) {
            currentHoverLine = document.createElementNS("http://www.w3.org/2000/svg", "line") as SVGLineElement
            currentHoverLine!!.setAttribute("y1", verticalBackgroundLines.firstOrNull()?.getAttribute("y1") ?: "43.0")
            currentHoverLine!!.setAttribute("y2", verticalBackgroundLines.firstOrNull()?.getAttribute("y2") ?: "425.0")
            currentHoverLine!!.setAttribute("style", "stroke:rgb(180, 180, 180);stroke-width:1.3")
        }

        if ((verticalBackground?.getAttribute("show-label-every-x")?.toDouble() ?: 1.0) > 1) {
            refreshBackgroundXLabelsDisplay()
        }
    }

    fun refreshCurrentHoverLineAndDataToolTip(mouseClientX: Double, mouseClientY: Double) {
        if (currentHoverLine != null) {
            var showToolTipData: DiagramData? = null
            if (isClientMouseInTransformArea(mouseClientX)) {
                var minGapX: Double = Double.MAX_VALUE
                val datasetMap = dataList.filter { it.g.hasAttribute("data-label") && it.g.style.display != "none" }.groupBy { it.dataset }
                datasetMap.forEach {
                    val dataList = it.value.filter { data -> round(parent.translateX(data.g.getBoundingClientRect().x + data.g.getBoundingClientRect().width / 2) * 100) / 100 in areaMinX..areaMaxX }
                    val diagramDataIndex = dataList.indexOf(dataList.findLast { data -> data.g.getBoundingClientRect().x + data.g.getBoundingClientRect().width / 2 <= mouseClientX })
                    if (diagramDataIndex != -1) {
                        val gap1 = abs(dataList[diagramDataIndex].g.getBoundingClientRect().x + dataList[diagramDataIndex].g.getBoundingClientRect().width / 2 - mouseClientX)
                        val gap2 = if (diagramDataIndex != dataList.size - 1) abs(dataList[diagramDataIndex + 1].g.getBoundingClientRect().x + dataList[diagramDataIndex + 1].g.getBoundingClientRect().width / 2 - mouseClientX) else Double.MAX_VALUE
                        val closestDiagramData = if (min(gap1, gap2) == gap1) dataList[diagramDataIndex] else dataList[diagramDataIndex + 1]
                        showToolTipData = if (min(minGapX, min(gap1, gap2)) == minGapX) showToolTipData else closestDiagramData
                        minGapX = min(minGapX, min(gap1, gap2))
                    }
                }
                if (showToolTipData != null) {
                    currentHoverLine!!.setAttribute("x1", showToolTipData!!.getShapeAttribute("cx")!!)
                    currentHoverLine!!.setAttribute("x2", showToolTipData!!.getShapeAttribute("cx")!!)
                    if (!g.contains(currentHoverLine)) {
                        g.appendChild(currentHoverLine!!)
                    }

                    var minGapY: Double = Double.MAX_VALUE
                    datasetMap.values.forEach {
                        val dataWithSameX = it.find { data -> data.getShapeAttribute("cx") == showToolTipData!!.getShapeAttribute("cx") }
                        if (dataWithSameX != null) {
                            val dataClientY = dataWithSameX.g.getBoundingClientRect().y + dataWithSameX.g.getBoundingClientRect().height / 2
                            val gapY = abs(dataClientY - mouseClientY)
                            if (min(minGapY, gapY) == gapY) {
                                showToolTipData = dataWithSameX
                                minGapY = gapY
                            }
                        }
                    }
                } else {
                    currentHoverLine!!.remove()
                }
            } else {
                currentHoverLine!!.remove()
            }
            parent.s.querySelectorAll(".diagram-tooltip").forEach { it.remove() }
            showToolTipData?.showTooltip()
        }
    }

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
        if (verticalBackgroundLines.isNotEmpty()) {
            val minX = areaMaxX - gapWidth - round(verticalBackgroundLines.last().getAttribute("x1")!!.toDouble())
            val maxX = areaMinX + gapWidth - round(verticalBackgroundLines.first().getAttribute("x1")!!.toDouble())
            val adjustedX = min(maxX, max(minX, x))
            g.setAttribute("scroll-x", adjustedX.toString())
            g.setAttribute("transform", "translate(${adjustedX},0.0)")
        }
    }

    private var zoomUpTimer: Int? = null
    private var zoomUpTargetCenterLineIndex: Int? = null
    fun zoom(mouseX: Double, isUp: Boolean) {
        if (verticalBackgroundLines.isNotEmpty()) {
            val currentMinLineIndex = verticalBackgroundLines.indexOf(verticalBackgroundLines.find { line -> round(parent.translateX(line.getBoundingClientRect().x)) >= areaMinX } ?: verticalBackgroundLines.first())
            val currentMaxLineIndex = verticalBackgroundLines.indexOf(verticalBackgroundLines.findLast { line -> round(parent.translateX(line.getBoundingClientRect().x)) <= areaMaxX } ?: verticalBackgroundLines.last())
            val changeGap = max(1, (currentMaxLineIndex - currentMinLineIndex) / 10)

            val newMinLineIndex: Int // will move this line as new first background vertical line
            val newMaxLineIndex: Int // will move this line as new last background vertical line
            if (isUp) { // zoom-up to a target area
                // the target area should always keep same (Around the line which is chosen at first)
                if (zoomUpTargetCenterLineIndex == null) {
                    zoomUpTargetCenterLineIndex = verticalBackgroundLines.indexOf(verticalBackgroundLines.find { line -> round(parent.translateX(line.getBoundingClientRect().x)) >= mouseX } ?: verticalBackgroundLines.last())
                }
                if (zoomUpTimer != null) {
                    window.clearTimeout(zoomUpTimer!!)
                }
                zoomUpTimer = window.setTimeout({
                    zoomUpTargetCenterLineIndex = null
                }, 500)

                val targetCenterLineIndex = zoomUpTargetCenterLineIndex ?: ((currentMinLineIndex + currentMaxLineIndex) / 2)
                val leftGapNumber = targetCenterLineIndex - currentMinLineIndex
                val rightGapNumber = currentMaxLineIndex - targetCenterLineIndex
                if (leftGapNumber != rightGapNumber) { // the line is not centered, so only zoom-up the bigger side
                    if (abs(leftGapNumber - rightGapNumber) - changeGap >= 0) { // changeGap is not enough to make the line centered, so zoom-up the side fully
                        if (leftGapNumber > rightGapNumber) {
                            newMinLineIndex = currentMinLineIndex + changeGap
                            newMaxLineIndex = currentMaxLineIndex
                        } else {
                            newMinLineIndex = currentMinLineIndex
                            newMaxLineIndex = currentMaxLineIndex - changeGap
                        }
                    } else { // changeGap is larger than the distance to make the line centered
                        val extraChangeGap = max(1, (changeGap - abs(leftGapNumber - rightGapNumber)) / 2)
                        if (leftGapNumber > rightGapNumber) {
                            newMaxLineIndex = currentMaxLineIndex - extraChangeGap
                            newMinLineIndex = max(0, targetCenterLineIndex * 2 - newMaxLineIndex)
                        } else {
                            newMinLineIndex = currentMinLineIndex + extraChangeGap
                            newMaxLineIndex = min(verticalBackgroundLines.size - 1, targetCenterLineIndex * 2 - currentMinLineIndex)
                        }
                    }
                } else { // the line is already centered, so zoom-up averagely
                    newMinLineIndex = currentMinLineIndex + changeGap
                    newMaxLineIndex = currentMaxLineIndex - changeGap
                }
            } else { // zoom-down averagely
                newMinLineIndex = max(0, currentMinLineIndex - changeGap)
                newMaxLineIndex = min(verticalBackgroundLines.size - 1, currentMaxLineIndex + changeGap)
                zoomUpTargetCenterLineIndex = null
            }

            if ((!isUp || newMaxLineIndex - newMinLineIndex > 2) && (currentMinLineIndex != newMinLineIndex || currentMaxLineIndex != newMaxLineIndex)) {
                val newMinLine = verticalBackgroundLines[newMinLineIndex] as SVGLineElement
                val newMaxLine = verticalBackgroundLines[newMaxLineIndex] as SVGLineElement
                val zoomRadio = (areaMaxX - areaMinX) / (newMaxLine.x1.baseVal.value - newMinLine.x1.baseVal.value)

                verticalBackgroundLines.forEach { line ->
                    val targetX = ((line.getAttribute("x1")?.toDouble() ?: areaMinX) - areaMinX) * zoomRadio + areaMinX
                    line.setAttribute("x1", targetX.toString())
                    line.setAttribute("x2", targetX.toString())
                }
                refreshBackgroundXLabelsPosition(zoomRadio)
                refreshBackgroundXLabelsDisplay(zoomRadio)
                refreshDataShape(zoomRadio)
                scrollTo(areaMinX - newMinLine.x1.baseVal.value)
            }
        }
    }

    private var zoomTimer: Int? = null
    private fun refreshBackgroundXLabelsDisplay(zoomRadio: Double = 1.0) {
        val showLabelEveryX = (verticalBackground!!.getAttribute("show-label-every-x")?.toDouble() ?: 1.0) / zoomRadio
        verticalBackground.setAttribute("show-label-every-x", showLabelEveryX.toString())

        // refresh the display only after stopping Zoom
        if (zoomTimer != null) {
            window.clearTimeout(zoomTimer!!)
        }
        zoomTimer = window.setTimeout({
            verticalBackgroundTexts.forEachIndexed { index, text ->
                (text as SVGTextElement).style.display = if (index % ceil(round(showLabelEveryX * 100) / 100).toInt() == 0) "" else "none"
            }
        }, if (zoomRadio == 1.0) 0 else 500)
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

    private fun refreshDataShape(zoomRadio: Double = 1.0) { // todo: should also move the data counts (vertically and horizontally)
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

    fun isClientMouseInTransformArea(mouseClientX: Double): Boolean {
        return parent.translateX(mouseClientX) in areaMinX..(parent.s.viewBox.baseVal.x + parent.s.viewBox.baseVal.width)
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}