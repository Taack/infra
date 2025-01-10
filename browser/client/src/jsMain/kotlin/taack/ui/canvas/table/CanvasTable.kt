package taack.ui.canvas.table

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasFigure
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasTable(
    private val initHeaders: List<TxtHeaderCanvas> = listOf(),
    private val initCells: List<TxtRowCanvas> = listOf(),
    txt: String = "",
    private val initCitationNumber: Int = 0
) : ICanvasDrawable {

    private val rows = initCells.toMutableList()
    private val headers = initHeaders.toMutableList()
    var currentRowIndex: Int = 0
    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    private var isDrawn: Boolean = false
    private val columns
        get() = headers.size
    val text = CanvasFigure(txt, initCitationNumber)
    override var citationNumber: Int = initCitationNumber

    companion object {

        fun createTableFromAsciidoc(txt: String): CanvasTable {
            return createTable()
        }

        fun createTable() = CanvasTable(
            listOf(
                TxtHeaderCanvas("Header 1"),
                TxtHeaderCanvas("Header 2"),
            ), listOf(
                TxtRowCanvas("Cell 1"),
                TxtRowCanvas("Cell 2"),
            ), "New Table", 0
        )
    }

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText {
        if (posX == null || posY == null) {
            return text
        } else if (isDrawn) {
            currentRowIndex = 0
            for (r in headers + rows) {
                if (posY in r.globalPosYStart..r.globalPosYEnd && posX in r.posXStart..r.posXEnd) {
                    return r
                }
                currentRowIndex++
            }
            currentRowIndex++
            return text
        } else {
            val t = (headers + rows)
            if (t.size > currentRowIndex) {
                return t[currentRowIndex]
            }
        }
        return text
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        isDrawn = false
        traceIndent("CanvasTable::draw: $posX, $posY, $width")
        ctx.save()
        globalPosYStart = posY
        var y = posY + 10.0
        val w = width - 35.0 - citationNumber * 16.0
        for (j in 0..<(headers + rows).size step columns) {
            var hMax = 0.0
            for (c in 0..<columns) {
                val i = c + j
                if (i < headers.size) {
                    val h = headers[i].draw(
                        ctx,
                        (i % columns + 1) * w / columns,
                        y,
                        citationNumber * 16.0 + 20.0 + (i % columns).toDouble() * w / columns
                    ) - y
                    hMax = maxOf(hMax, h)
                } else {
                    val h = rows[i - headers.size].draw(
                        ctx,
                        (i % columns + 1) * w / columns,
                        y,
                        citationNumber * 16.0 + 20.0 + (i % columns).toDouble() * w / columns
                    ) - y
                    hMax = maxOf(hMax, h)
                }
            }
            for (c in 0..<columns) {
                val i = c + j

                if (i < columns) {

                    ctx.save()
                    ctx.fillStyle = "#58b2ee11"
                    ctx.strokeStyle = "#ffffff"
                    ctx.fillRect(
                        (citationNumber * 16.0 - j % columns) + 10.0 + (i % columns).toDouble() * w / columns,
                        y,
                        w / columns,
                        hMax
                    )
                }
                ctx.save()
                ctx.fillStyle = "#11111111"
                ctx.strokeRect(
                    (citationNumber * 16.0 - j % columns) + 10.0 + (i % columns).toDouble() * w / columns,
                    y,
                    w / columns,
                    hMax
                )
                ctx.restore()
            }
            y += hMax
        }
        ctx.restore()
        drawCitation(ctx, y, y - posY)
        globalPosYEnd = y
        globalPosYEnd = text.draw(ctx, width, globalPosYEnd, posX)
        traceDeIndent("CanvasTable::draw: $globalPosYEnd")
        isDrawn = true
        return globalPosYEnd
    }

    override fun dumpAsciidoc(): String {
        val ret = StringBuilder()
        ret.append("\n|===\n|")
        for (h in headers) {
            ret.append(h.dumpAsciidoc())
            if (headers.indexOf(h) < headers.size - 1)
                ret.append("|")
        }
        ret.append("\n")
        for (r in rows) {
            ret.append("\n|${r.dumpAsciidoc()}")
        }
        ret.append("\n|===\n")
        return ret.toString()
    }

    override fun reset() {
        trace("CanvasTable::reset before ${headers.size} ${rows.size}")
        headers.forEach { it.reset() }
        headers.clear()
        headers.addAll(initHeaders)
        rows.forEach { it.reset() }
        rows.clear()
        rows.addAll(initCells)
        text.reset()
        citationNumber = initCitationNumber
        trace("CanvasTable::reset after ${headers.size} ${rows.size}")
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        traceIndent("CanvasTable::click: $posX, $posY")
        for (r in headers + rows) {
            if (r.isClicked(posX, posY)) {
                traceDeIndent("CanvasTable::click: $r, $posX, $posY")
                return r.click(ctx, posX, posY)
            }
        }
        if (text.isClicked(posX, posY)) {
            traceDeIndent("CanvasTable::click: $text, $posX, $posY")
            return text.click(ctx, posX, posY)
        }
        traceDeIndent("CanvasTable::click: null")
        return null
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        traceIndent("CanvasTable::doubleClick: $posX, $posY")
        for (r in headers + rows) {
            if (r.isClicked(posX, posY)) {
                traceDeIndent("CanvasTable::doubleClick: $r, $posX, $posY")
                return r.doubleClick(ctx, posX, posY)
            }
        }
        if (text.isClicked(posX, posY)) {
            traceDeIndent("CanvasTable::click: $text, $posX, $posY")
            return text.doubleClick(ctx, posX, posY)
        }
        traceDeIndent("CanvasTable::doubleClick: null")
        return null

    }

    fun addLine(currentText: TxtRowCanvas) {
        traceIndent("CanvasTable::addLine: $currentText ${rows.size}")
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                for (j in (0 until columns)) {
                    rows.add((i - (i % columns) + columns), TxtRowCanvas(""))
                }
                break
            }
        }
        traceDeIndent("CanvasTable::addLine: ${rows.size}")
    }

    fun removeLine(currentText: TxtRowCanvas) {
        if (rows.size <= 2 * columns) return
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                for (j in (0 until columns)) {
                    rows.removeAt(i - (i % columns) + columns)
                }
                break
            }
        }
    }

    fun addColumn(currentText: TxtHeaderCanvas) {
        for (i in (headers.indices)) {
            if (headers[i] == currentText) {
                headers.add(i + 1, TxtHeaderCanvas(""))
                for (j in ((i + 1) until rows.size step columns)) {
                    rows.add(j, TxtRowCanvas(""))
                }
                break
            }
        }
    }

    fun removeColumn(currentText: TxtHeaderCanvas) {
        if (rows.size <= 4) return
        for (i in (rows.indices)) {
            if (headers[i] == currentText) {
                rows.removeAt(i)
                var n = 0
                for (j in ((columns + i) until (rows.size) step columns)) {
                    trace("CanvasTable::removeColumn: $j, on ${rows.size}")
                    rows.removeAt(j - n++)
                }
                break
            }
        }
    }

}