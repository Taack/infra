package taack.ui.diagram

import web.svg.SVGGElement

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

    fun hideOrShowDataset(toShow: Boolean, dataset: String) {
        dataList.filter { it.dataset == dataset }.forEach {
            it.hideOrShow(toShow)
        }

        // manage dataShape position/width/height
        if (shapeType in listOf("bar", "stackedBar", "whiskers")) {
            val dataListGroupedByGap = dataList.filter { it.gapIndex != null }.groupBy { it.gapIndex }
            val gapWidth = (areaMaxX - areaMinX) / dataListGroupedByGap.size
            for (gapIndex in dataListGroupedByGap.keys) {
                val displayedDataset: Map<String, List<DiagramData>> = dataListGroupedByGap[gapIndex]!!.filter { it.g.style.display != "none" }.groupBy { it.dataset }
                if (displayedDataset.isNotEmpty()) {
                    if (shapeType in listOf("bar", "whiskers")) {
                        val shapeNumber = displayedDataset.size
                        var gapHorizontalPadding: Double = gapWidth * 0.2 / 2
                        var shapeWidth: Double = if (shapeNumber > 1) (gapWidth * 0.8) * 0.8 / shapeNumber else gapWidth * 0.8
                        val shapeMargin: Double  = if (shapeNumber > 1) (gapWidth * 0.8) * 0.2 / (shapeNumber - 1) else 0.0
                        if (maxShapeWidth > 0 && shapeWidth > maxShapeWidth) {
                            shapeWidth = maxShapeWidth
                            gapHorizontalPadding = (gapWidth - shapeWidth * shapeNumber - shapeMargin * (shapeNumber - 1)) / 2
                        }

                        for (key in displayedDataset.keys) {
                            val index = displayedDataset.keys.indexOf(key)
                            displayedDataset[key]!!.forEach {
                                it.moveShapeHorizontally(areaMinX + gapWidth * gapIndex!! + gapHorizontalPadding + (shapeWidth + shapeMargin) * index, shapeWidth)
                            }
                        }
                    } else if (shapeType == "stackedBar") {
                        var bottomY: Double = areaMaxY
                        for (key in displayedDataset.keys) {
                            displayedDataset[key]!!.forEach {
                                bottomY = it.moveShapeVertically(bottomY)
                            }
                        }
                    }
                }
            }
        }
    }

    fun scroll(movingDistance: Double) {
        if (shapeType != "area") {
            val targetTransformX = (g.getAttribute("scroll-x")?.toDouble() ?: 0.0) + movingDistance
            g.setAttribute("scroll-x", targetTransformX.toString())
            g.setAttribute("transform", "translate(${targetTransformX},0.0)")
        }
    }

    fun getDiagramRoot(): Diagram {
        return parent.getDiagramRoot()
    }
}