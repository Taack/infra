package taack.ui.diagram

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.element.Block
import web.svg.*
import kotlin.math.*

class DiagramDataContainer(val parent: Diagram, val g: SVGGElement): BaseElement {
    companion object {
        fun getSiblingDiagramDataContainer(d: Diagram): List<DiagramDataContainer> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='DATA_CONTAINER']").asList()
            return elements.map {
                DiagramDataContainer(d, it as SVGGElement)
            }
        }
    }

    private val dataList: List<DiagramData> = DiagramData.getSiblingDiagramData(this)
    private val shapeType: String = g.attributes.getNamedItem("shape-type")!!.value
    private val maxShapeWidth: Double = g.attributes.getNamedItem("shape-max-width")!!.value.toDouble()

    fun hideOrShowDataset(toShow: Boolean, dataset: String) {
        dataList.filter { it.dataset == dataset }.forEach {
            it.hideOrShow(toShow)
        }
        refreshDataShape()
    }

    fun refreshDataShape(zoomRadio: Double = 1.0) {
        val gapWidth = parent.verticalBackground!!.gapWidth
        val areaMinX = parent.verticalBackground.areaMinX
        val areaMaxY = parent.horizontalBackgrounds.first().areaMaxY
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
            "timeline" -> {
                if (zoomRadio != 1.0) {
                    dataList.forEach {
                        val targetX = (it.getShapeAttribute("x")!!.toDouble() - areaMinX) * zoomRadio + areaMinX
                        val targetWidth = it.getShapeAttribute("width")!!.toDouble() * zoomRadio
                        it.moveShapeHorizontally(targetX, targetWidth)
                    }
                }
            }
            "area" -> {
                if (zoomRadio != 1.0) {
                    dataList.forEach {
                        it.moveShapeHorizontally(areaMinX, zoomRadio)
                    }
                }
            }
            else -> {}
        }
    }

    fun getShapeType(): String {
        return shapeType
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}