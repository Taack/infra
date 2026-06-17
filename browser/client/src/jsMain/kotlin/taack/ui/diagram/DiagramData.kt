package taack.ui.diagram

import js.array.asList
import taack.ui.base.LeafElement
import web.svg.*

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
    private val dataLabels: MutableList<SVGTextElement> = mutableListOf()

    init {
        var dataLabel = (getTooltip()?.g ?: g).nextElementSibling
        while (dataLabel?.tagName == "text") {
            dataLabels.add(dataLabel as SVGTextElement)
            dataLabel = dataLabel.nextElementSibling
        }
    }

    fun hideOrShow(toShow: Boolean) {
        g.style.display = if (toShow) "" else "none"
        dataLabels.forEach { dataLabel ->
            dataLabel.style.display = if (toShow) "" else "none"
        }
    }

    fun moveShapeHorizontally(startX: Double, shapeWidth: Double) {
        shapes.forEach { shape ->
            when (shape.tagName) {
                "rect" -> {
                    val oldX = shape.getAttribute("x")!!.toDouble()
                    val oldWidth = shape.getAttribute("width")!!.toDouble()
                    shape.setAttribute("x", startX.toString())
                    shape.setAttribute("width", shapeWidth.toString())
                    dataLabels.forEach { dataLabel ->
                        val labelX = dataLabel.getAttribute("x")!!.toDouble()
                        val labelWidth = dataLabel.getAttribute("label-width")!!.toDouble()
                        if (labelX + labelWidth < oldX) { // startDate at left
                            dataLabel.setAttribute("x", (startX + (labelX + labelWidth - oldX) - labelWidth).toString())
                        } else if (labelX > oldX + oldWidth) { // endDate at right
                            dataLabel.setAttribute("x", (startX + shapeWidth + (labelX - oldX - oldWidth)).toString())
                        } else { // data inside at middle
                            dataLabel.setAttribute("x", (startX + (shapeWidth - labelWidth) / 2).toString())
                        }
                    }
                }
                "circle" -> {
                    shape.setAttribute("cx", startX.toString())
                    dataLabels.forEach { dataLabel ->
                        if (parent.getShapeType() == "line") {
                            dataLabel.setAttribute("x", (startX - dataLabel.getAttribute("label-width")!!.toDouble() / 2).toString())
                        } else if (parent.getShapeType() == "scatter") {
                            dataLabel.setAttribute("x", (startX + shape.getAttribute("r")!!.toDouble() + 2.0).toString())
                        }
                    }
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
                    dataLabels.forEach { dataLabel ->
                        val fontSize = dataLabel.style.fontSize.let { it.substring(0, it.indexOf("px")) }.toDouble()
                        dataLabel.setAttribute("y", (y + shape.getAttribute("height")!!.toDouble() / 2 + fontSize / 2 - 2.0).toString())
                    }
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

    fun getTooltip(): DiagramTooltip? {
        val tooltip = g.closest("g[element-type='TOOLTIP']")
        return if (tooltip != null) {
            parent.parent.tooltips.firstOrNull { it.g == tooltip }
        } else {
            null
        }
    }
}