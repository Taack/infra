package taack.ui.diagram

import js.array.asList
import web.svg.SVGGElement

class DiagramDataGroup(val parent: Diagram, val g: SVGGElement) {
    companion object {
        fun getSiblingDiagramDataGroup(d: Diagram): List<DiagramDataGroup> {
            val elements: List<*> = d.s.querySelectorAll("g[element-type='DATA_GROUP']").asList()
            return elements.map {
                DiagramDataGroup(d, it as SVGGElement)
            }
        }
    }

    private val dataList: List<DiagramData> = DiagramData.getSiblingDiagramData(this)

    // non-stacked bar, whiskers
    private val startX: Double? = g.attributes.getNamedItem("start-x")?.value?.toDouble()
    private val gapWidth: Double? = g.attributes.getNamedItem("gap-width")?.value?.toDouble()
    private val maxShapeWidth: Double? = g.attributes.getNamedItem("max-shape-width")?.value?.toDouble()

    // stacked bar
    private val startY: Double? = g.attributes.getNamedItem("start-y")?.value?.toDouble()

    fun hideOrShowDataset(toShow: Boolean, dataset: String) {
        dataList.filter { it.dataset == dataset }.forEach {
            it.hideOrShow(toShow)
        }

        // manage dataShape position/width/height
        val displayedData: List<DiagramData> = dataList.filter { it.g.style.display != "none" }
        if (displayedData.isNotEmpty()) {
            if (gapWidth != null && startX != null) { // non-stacked bar, whiskers
                val dataNumber = displayedData.size
                val shapeMargin: Double  = if (dataNumber > 1) (gapWidth * 0.8) * 0.2 / (dataNumber - 1) else 0.0
                var shapeWidth: Double = if (dataNumber > 1) (gapWidth * 0.8) * 0.8 / dataNumber else gapWidth * 0.8
                var gapHorizontalPadding: Double = gapWidth * 0.2 / 2
                if (maxShapeWidth != null && shapeWidth > maxShapeWidth) {
                    shapeWidth = maxShapeWidth
                    gapHorizontalPadding = (gapWidth - shapeWidth * dataNumber - shapeMargin * (dataNumber - 1)) / 2
                }
                for (i in displayedData.indices) {
                    displayedData[i].moveShapeHorizontally(startX + gapHorizontalPadding + (shapeWidth + shapeMargin) * i, shapeWidth)
                }
            } else if (startY != null) { // stacked bar
                var bottomY: Double = startY
                for (i in displayedData.indices) {
                    bottomY = displayedData[i].moveShapeVertically(bottomY)
                }
            }
        }
    }

    fun getDiagramRoot(): Diagram {
        return parent.getDiagramRoot()
    }
}